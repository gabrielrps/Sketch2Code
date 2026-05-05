package it.skecto2code.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Service
public class SketchToCodeService {

    private final ChatClient chatClient;

    @Value("${sketch2code.vision.model}")
    private String visionModel;

    @Value("${sketch2code.vision.temperature}")
    private double visionTemperature;

    @Value("${sketch2code.code.model}")
    private String codeModel;

    @Value("${sketch2code.code.temperature}")
    private double codeTemperature;


    public SketchToCodeService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String generateHtmlFromSketch(byte[] imageBytes, String contentType) {
        String imageDescription = getImageDescription(imageBytes, contentType);
        return getHtmlFromImageDescription(imageDescription);
    }

    private String getHtmlFromImageDescription(String imageDescription) {
        OllamaChatOptions codeOptions = OllamaChatOptions.builder()
                .model(codeModel)
                .temperature(codeTemperature)
                .build();

        UserMessage codeUserMessage = UserMessage.builder()
                .text("Convert this description to HTML/Bootstrap 5: " + imageDescription)
                .build();

        String codePrompt = """
                You are a code exporter.
                Respond ONLY with HTML5 and Bootstrap 5 code.
                Do not use Markdown blocks (```html).
                Do not provide explanations.
                Your response must begin directly with <!DOCTYPE html>.
                IMPORTANT: Generate ONLY what is described. Do not add elements, sections, or content that are not explicitly mentioned in the description.
                IMPORTANT: Apply colors exactly as described. If a color is mentioned for any element, use that exact color in the inline style or Bootstrap utility class.
                """;

        return chatClient.prompt()
                .options(codeOptions)
                .system(codePrompt)
                .messages(codeUserMessage)
                .call()
                .content();
    }

    private String getImageDescription(byte[] imageBytes, String contentType) {
        OllamaChatOptions visionOptions = OllamaChatOptions.builder()
                .model(visionModel)
                .temperature(visionTemperature)
                .build();

        Media media = Media.builder()
                .mimeType(contentType.equals(MimeTypeUtils.IMAGE_JPEG_VALUE) ? MimeTypeUtils.IMAGE_JPEG : MimeTypeUtils.IMAGE_PNG)
                .data(imageBytes)
                .build();

        String visionSystemPrompt = """
                You are a UI analyst. Describe only what is literally visible in the sketch.
                Output a structured list. For each element include:
                - Type (button, input, heading, paragraph, image, navbar, table, etc.)
                - Position (top/middle/bottom and left/center/right)
                - Label or visible text (if any)
                - Color (if explicitly visible or written)
                Never infer or invent elements that are not drawn. Never add suggestions or improvements.
                """;

        UserMessage visionMessage = UserMessage.builder()
                .text("List every visible UI element in this sketch following the format in your instructions.")
                .media(media)
                .build();

        return chatClient.prompt()
                .options(visionOptions)
                .system(visionSystemPrompt)
                .messages(visionMessage)
                .call()
                .content();
    }

}
