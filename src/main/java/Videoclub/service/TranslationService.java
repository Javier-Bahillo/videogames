package Videoclub.service;

public interface TranslationService {
    String translateToSpanish(String text);
    String translate(String text, String targetLang);
}
