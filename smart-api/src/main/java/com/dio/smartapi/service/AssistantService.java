package com.dio.smartapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssistantService {

    private final ChatClient chatClient;
    private final OpenAiAudioTranscriptionModel transcriptionModel;
    private final OpenAiAudioSpeechModel speechModel;
    private final TransactionService transactionService;

    private static final String SYSTEM_PROMPT = """
            Você é um assistente financeiro inteligente chamado "FinBot".
            Você ajuda o usuário a registrar e consultar transações financeiras.
            
            Quando o usuário pedir para registrar uma transação:
            - Identifique a descrição, valor e tipo (INCOME para receita, EXPENSE para despesa)
            - Use a ferramenta registerTransaction para salvar no banco de dados
            
            Quando o usuário perguntar sobre saldo ou resumo financeiro:
            - Use a ferramenta getBalance para obter os dados
            
            Responda sempre em português, de forma clara e amigável.
            """;

    /**
     * Envia uma mensagem de texto para o ChatModel (GPT) com Tool Calling habilitado.
     */
    public String chat(String userMessage) {
        log.info("Processando mensagem: {}", userMessage);

        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userMessage)
                .functions("registerTransaction", "getBalance")
                .call()
                .content();
    }

    /**
     * Transcreve um arquivo de áudio usando o modelo Whisper da OpenAI.
     */
    public String transcribeAudio(MultipartFile audioFile) throws IOException {
        log.info("Transcrevendo áudio: {}", audioFile.getOriginalFilename());

        Resource audioResource = new ByteArrayResource(audioFile.getBytes()) {
            @Override
            public String getFilename() {
                return audioFile.getOriginalFilename();
            }
        };

        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource);
        AudioTranscriptionResponse response = transcriptionModel.call(prompt);

        String transcription = response.getResult().getOutput();
        log.info("Transcrição concluída: {}", transcription);
        return transcription;
    }

    /**
     * Converte texto em áudio usando Text-to-Speech da OpenAI.
     */
    public byte[] textToSpeech(String text) {
        log.info("Convertendo texto para fala: {}", text);

        SpeechPrompt speechPrompt = new SpeechPrompt(text);
        SpeechResponse response = speechModel.call(speechPrompt);

        return response.getResult().getOutput();
    }

    /**
     * Pipeline completo: recebe áudio → transcreve → processa com IA → responde em áudio.
     */
    public byte[] processAudioCommand(MultipartFile audioFile) throws IOException {
        // 1. Transcrever áudio para texto
        String transcription = transcribeAudio(audioFile);
        log.info("Comando transcrito: {}", transcription);

        // 2. Processar comando com ChatModel + Tool Calling
        String aiResponse = chat(transcription);
        log.info("Resposta da IA: {}", aiResponse);

        // 3. Converter resposta em áudio
        return textToSpeech(aiResponse);
    }
}
