package com.dio.smartapi.controller;

import com.dio.smartapi.service.AssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
@Tag(name = "Assistant", description = "Assistente de IA com reconhecimento de fala")
public class AssistantController {

    private final AssistantService assistantService;

    /**
     * Endpoint de chat por texto com Tool Calling.
     * Exemplo: POST /api/assistant/chat
     * Body: { "message": "registre uma despesa de R$50 de almoço" }
     */
    @PostMapping("/chat")
    @Operation(summary = "Envia mensagem de texto para o assistente de IA")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String response = assistantService.chat(message);
        return ResponseEntity.ok(Map.of("response", response));
    }

    /**
     * Endpoint de transcrição de áudio (Whisper).
     * Exemplo: POST /api/assistant/transcribe
     * Multipart: file = arquivo de áudio (.mp3, .wav, .m4a)
     */
    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Transcreve um arquivo de áudio para texto")
    public ResponseEntity<Map<String, String>> transcribe(
            @RequestParam("file") MultipartFile audioFile) throws IOException {
        String transcription = assistantService.transcribeAudio(audioFile);
        return ResponseEntity.ok(Map.of("transcription", transcription));
    }

    /**
     * Endpoint de Text-to-Speech.
     * Exemplo: POST /api/assistant/speak
     * Body: { "text": "Olá! Saldo atual é R$1000" }
     * Retorna: arquivo de áudio MP3
     */
    @PostMapping(value = "/speak", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Converte texto em áudio (Text-to-Speech)")
    public ResponseEntity<byte[]> speak(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        byte[] audio = assistantService.textToSpeech(text);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(audio);
    }

    /**
     * Endpoint do pipeline completo: áudio → transcrição → IA → áudio.
     * Exemplo: POST /api/assistant/voice-command
     * Multipart: file = arquivo de áudio com o comando do usuário
     * Retorna: áudio com a resposta da IA
     */
    @PostMapping(value = "/voice-command",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Pipeline completo: áudio → IA → áudio de resposta")
    public ResponseEntity<byte[]> voiceCommand(
            @RequestParam("file") MultipartFile audioFile) throws IOException {
        byte[] responseAudio = assistantService.processAudioCommand(audioFile);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(responseAudio);
    }
}
