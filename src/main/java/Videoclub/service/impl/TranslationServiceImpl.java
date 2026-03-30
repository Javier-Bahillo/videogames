package Videoclub.service.impl;

import Videoclub.dto.gamesdb.MyMemoryResponse;
import Videoclub.service.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    private final RestTemplate restTemplate;

    private static final String MYMEMORY_URL = "https://api.mymemory.translated.net/get";
    private static final int MAX_CHARS = 500;

    //Diccionario Quenya/Sindarin para traducción élfica
    private static final Map<String, String> ELVISH_DICT = Map.ofEntries(
        // Artículos / conjunciones / preposiciones
        Map.entry("the", "i"),        Map.entry("a", "er"),         Map.entry("an", "er"),
        Map.entry("and", "ar"),       Map.entry("or", "vel"),       Map.entry("of", "o"),
        Map.entry("in", "mi"),        Map.entry("to", "na"),        Map.entry("into", "mi"),
        Map.entry("with", "as"),      Map.entry("from", "et"),      Map.entry("by", "an"),
        Map.entry("for", "an"),       Map.entry("on", "bo"),        Map.entry("at", "nó"),
        Map.entry("about", "epë"),    Map.entry("over", "or"),      Map.entry("under", "nu"),
        Map.entry("after", "epë"),    Map.entry("before", "epë"),   Map.entry("through", "ter"),
        Map.entry("between", "imbi"), Map.entry("against", "or"),   Map.entry("without", "lá"),
        // Pronombres
        Map.entry("i", "ni"),         Map.entry("me", "ni"),        Map.entry("my", "ninya"),
        Map.entry("you", "lye"),      Map.entry("your", "lúnya"),
        Map.entry("he", "ho"),        Map.entry("him", "hom"),      Map.entry("his", "hónya"),
        Map.entry("she", "hë"),       Map.entry("her", "hënya"),
        Map.entry("we", "ve"),        Map.entry("us", "me"),        Map.entry("our", "menya"),
        Map.entry("they", "te"),      Map.entry("them", "te"),      Map.entry("their", "tenya"),
        Map.entry("it", "sa"),        Map.entry("its", "sanya"),
        Map.entry("this", "sina"),    Map.entry("that", "tana"),
        Map.entry("these", "sinë"),   Map.entry("those", "tanë"),
        Map.entry("what", "man"),     Map.entry("who", "mo"),       Map.entry("which", "ya"),
        Map.entry("where", "ya"),     Map.entry("when", "nán"),     Map.entry("how", "mana"),
        // Verbos
        Map.entry("is", "ná"),        Map.entry("was", "né"),       Map.entry("are", "nar"),
        Map.entry("were", "narë"),    Map.entry("be", "ná"),        Map.entry("been", "naië"),
        Map.entry("have", "harya"),   Map.entry("has", "harya"),    Map.entry("had", "haryanë"),
        Map.entry("do", "car"),       Map.entry("does", "cara"),    Map.entry("did", "carnë"),
        Map.entry("will", "uva"),     Map.entry("would", "uvanen"), Map.entry("could", "pole"),
        Map.entry("should", "nai"),   Map.entry("can", "pol"),      Map.entry("not", "lá"),
        Map.entry("no", "lá"),        Map.entry("never", "lá"),
        Map.entry("go", "lenda"),     Map.entry("come", "tul"),     Map.entry("make", "car"),
        Map.entry("give", "anta"),    Map.entry("find", "hir"),     Map.entry("know", "ista"),
        Map.entry("see", "cen"),      Map.entry("look", "tirë"),    Map.entry("say", "quet"),
        Map.entry("play", "tyalë"),   Map.entry("fight", "mahtar"), Map.entry("protect", "varya"),
        Map.entry("explore", "randir"), Map.entry("discover", "hir"), Map.entry("seek", "hir"),
        Map.entry("return", "nosta"), Map.entry("fall", "lanta"),   Map.entry("rise", "orta"),
        Map.entry("follow", "hilya"), Map.entry("lead", "tur"),     Map.entry("rule", "tur"),
        Map.entry("speak", "quet"),   Map.entry("sing", "linda"),   Map.entry("run", "rín"),
        Map.entry("walk", "vanta"),   Map.entry("die", "firë"),     Map.entry("live", "coirë"),
        Map.entry("defeat", "vanwa"), Map.entry("escape", "lenda"), Map.entry("win", "orm"),
        // Naturaleza
        Map.entry("world", "Ambar"),  Map.entry("earth", "Cemen"),  Map.entry("sky", "Menel"),
        Map.entry("sun", "Anar"),     Map.entry("moon", "Isil"),    Map.entry("star", "elen"),
        Map.entry("stars", "eleni"),  Map.entry("light", "calë"),   Map.entry("shadow", "mórë"),
        Map.entry("dark", "mórë"),    Map.entry("darkness", "mórë"), Map.entry("fire", "nár"),
        Map.entry("water", "nen"),    Map.entry("wind", "súrë"),    Map.entry("air", "vílë"),
        Map.entry("tree", "ornë"),    Map.entry("trees", "orneli"), Map.entry("forest", "taur"),
        Map.entry("mountain", "oron"), Map.entry("mountains", "oronti"), Map.entry("river", "sírë"),
        Map.entry("sea", "ëar"),      Map.entry("ocean", "ëarë"),   Map.entry("land", "nórë"),
        Map.entry("stone", "ondo"),   Map.entry("night", "lómë"),   Map.entry("day", "aurë"),
        Map.entry("time", "lúmë"),    Map.entry("age", "yén"),      Map.entry("space", "Menel"),
        // Abstracto / emociones
        Map.entry("life", "coirë"),   Map.entry("death", "firë"),   Map.entry("love", "melmë"),
        Map.entry("hope", "estel"),   Map.entry("fate", "mandos"),  Map.entry("power", "vórë"),
        Map.entry("wisdom", "nólë"),  Map.entry("knowledge", "ista"), Map.entry("truth", "anwa"),
        Map.entry("glory", "alcar"),  Map.entry("courage", "vórë"), Map.entry("strength", "tûr"),
        Map.entry("spirit", "fëa"),   Map.entry("soul", "fëa"),     Map.entry("heart", "óre"),
        Map.entry("mind", "sánar"),   Map.entry("memory", "yéninya"), Map.entry("dream", "olórë"),
        Map.entry("blood", "serë"),   Map.entry("name", "essë"),    Map.entry("peace", "sérë"),
        Map.entry("war", "ohta"),     Map.entry("battle", "ohta"),  Map.entry("honor", "arwa"),
        // Personas / roles
        Map.entry("friend", "meldo"), Map.entry("enemy", "cotto"),
        Map.entry("king", "aran"),    Map.entry("queen", "tári"),
        Map.entry("lord", "heru"),    Map.entry("lady", "heri"),    Map.entry("hero", "cauno"),
        Map.entry("warrior", "ohtar"), Map.entry("wizard", "istar"), Map.entry("master", "heru"),
        Map.entry("elf", "elda"),     Map.entry("elves", "eldali"), Map.entry("human", "atan"),
        Map.entry("humans", "atani"), Map.entry("man", "atan"),     Map.entry("men", "atani"),
        Map.entry("woman", "nísë"),   Map.entry("child", "yondo"),  Map.entry("children", "yondoli"),
        Map.entry("player", "tyalindo"), Map.entry("character", "quénna"),
        // Videojuegos / aventura
        Map.entry("game", "tyalë"),   Map.entry("games", "tyaleli"), Map.entry("gameplay", "tyalë"),
        Map.entry("story", "quenta"), Map.entry("legend", "quenta"), Map.entry("tale", "quenta"),
        Map.entry("quest", "andavórë"), Map.entry("journey", "lendë"), Map.entry("adventure", "andavórë"),
        Map.entry("mission", "andavórë"), Map.entry("sword", "macil"), Map.entry("shield", "tarca"),
        Map.entry("bow", "quinga"),   Map.entry("arrow", "pilin"),  Map.entry("armor", "harma"),
        Map.entry("magic", "sanë"),   Map.entry("spell", "sanë"),   Map.entry("dragon", "lócë"),
        Map.entry("orc", "orch"),     Map.entry("tower", "minas"),  Map.entry("castle", "minas"),
        Map.entry("city", "ostë"),    Map.entry("kingdom", "aran"), Map.entry("realm", "aran"),
        Map.entry("path", "tië"),     Map.entry("road", "tië"),     Map.entry("door", "fen"),
        Map.entry("key", "ango"),     Map.entry("secret", "dolen"), Map.entry("treasure", "maltë"),
        Map.entry("gold", "laurë"),   Map.entry("silver", "tyelpë"), Map.entry("map", "randir"),
        Map.entry("skill", "istya"),  Map.entry("ability", "vórë"), Map.entry("level", "talta"),
        Map.entry("combat", "ohta"),  Map.entry("stealth", "dolen"), Map.entry("strategy", "nólë"),
        Map.entry("puzzle", "nólë"),  Map.entry("horror", "mórë"),  Map.entry("fantasy", "sanë"),
        Map.entry("platform", "cemenë"), Map.entry("console", "cemenë"),
        // Adjetivos
        Map.entry("great", "alta"),   Map.entry("small", "pitya"),  Map.entry("good", "mára"),
        Map.entry("evil", "úmára"),   Map.entry("old", "yerna"),    Map.entry("new", "vinya"),
        Map.entry("ancient", "yerna"), Map.entry("eternal", "firimë"), Map.entry("first", "minya"),
        Map.entry("last", "telda"),   Map.entry("long", "anda"),    Map.entry("high", "alta"),
        Map.entry("white", "faina"),  Map.entry("black", "mórë"),   Map.entry("red", "carnë"),
        Map.entry("blue", "lurë"),    Map.entry("green", "laiquë"), Map.entry("bright", "calima"),
        Map.entry("many", "rimba"),   Map.entry("all", "ilya"),     Map.entry("true", "anwa"),
        Map.entry("real", "anwa"),    Map.entry("free", "lerta"),   Map.entry("open", "panta"),
        Map.entry("lost", "vanwa"),   Map.entry("hidden", "dolen"), Map.entry("powerful", "vórëa"),
        Map.entry("mighty", "beleg"), Map.entry("brave", "virya"),  Map.entry("wise", "saila"),
        Map.entry("swift", "lirpa"),  Map.entry("fierce", "norna"),
        // Números
        Map.entry("one", "er"),       Map.entry("two", "atta"),     Map.entry("three", "neldë"),
        Map.entry("four", "canta"),   Map.entry("five", "lempë"),   Map.entry("six", "enquë"),
        Map.entry("seven", "otso"),   Map.entry("eight", "tolto"),  Map.entry("nine", "nertë"),
        Map.entry("ten", "quëan"),
        // Miscelánea
        Map.entry("voice", "quetil"), Map.entry("song", "lindë"),   Map.entry("word", "quetta"),
        Map.entry("home", "már"),     Map.entry("end", "tel"),      Map.entry("beginning", "etta"),
        Map.entry("people", "lië"),   Map.entry("hand", "már"),     Map.entry("eye", "hen"),
        Map.entry("music", "lindë"),  Map.entry("sound", "lairë"),  Map.entry("experience", "ista"),
        Map.entry("history", "quenta"), Map.entry("genre", "lassë"), Map.entry("release", "panta"),
        Map.entry("version", "vinya"), Map.entry("science", "ista"), Map.entry("future", "vinya"),
        Map.entry("past", "yerna"),   Map.entry("present", "sina")
    );

    // API pública

    @Override
    public String translateToSpanish(String text) {
        return translate(text, "es");
    }

    @Override
    public String translate(String text, String targetLang) {
        if (text == null || text.isBlank()) return text;
        return switch (targetLang.toLowerCase()) {
            case "ja" -> translateViaMyMemory(text, "en|ja");
            case "elv" -> translateToElvish(text);
            default  -> translateViaMyMemory(text, "en|es");
        };
    }

    // MyMemory (ES / JA)

    private String translateViaMyMemory(String text, String langpair) {
        String input = text.length() > MAX_CHARS ? text.substring(0, MAX_CHARS) : text;
        String url = UriComponentsBuilder.fromHttpUrl(MYMEMORY_URL)
                .queryParam("q", input)
                .queryParam("langpair", langpair)
                .build(false)
                .toUriString();
        try {
            MyMemoryResponse response = restTemplate.getForObject(url, MyMemoryResponse.class);
            if (response != null
                    && response.getResponseData() != null
                    && Integer.valueOf(200).equals(response.getResponseStatus())) {
                return response.getResponseData().getTranslatedText();
            }
        } catch (Exception e) {
            log.warn("Translation ({}) failed, returning original: {}", langpair, e.getMessage());
        }
        return text;
    }

    // Traducción élfica (Quenya/Sindarin)

    private String translateToElvish(String text) {
        String[] words = text.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(translateWordToElvish(words[i]));
        }
        return sb.toString();
    }

    private String translateWordToElvish(String token) {
        // Separar puntuación inicial y final
        int start = 0;
        int end = token.length();
        StringBuilder leading = new StringBuilder();
        StringBuilder trailing = new StringBuilder();

        while (start < end && !Character.isLetterOrDigit(token.charAt(start))) {
            leading.append(token.charAt(start++));
        }
        while (end > start && !Character.isLetterOrDigit(token.charAt(end - 1))) {
            trailing.insert(0, token.charAt(--end));
        }
        if (start >= end) return token;

        String word = token.substring(start, end);
        boolean isCapitalized = Character.isUpperCase(word.charAt(0));
        boolean isAllCaps = word.equals(word.toUpperCase()) && word.length() > 1;

        String lower = word.toLowerCase();
        String translated = ELVISH_DICT.getOrDefault(lower, applyElvishPhonetics(lower));

        // Restaurar capitalización
        if (isAllCaps && translated.length() > 1) {
            translated = translated.toUpperCase();
        } else if (isCapitalized && !translated.isEmpty()) {
            translated = Character.toUpperCase(translated.charAt(0)) + translated.substring(1);
        }

        return leading + translated + trailing;
    }

    private String applyElvishPhonetics(String word) {
        String stem = word;

        // Transformaciones de sufijos
        String suffix;
        if (stem.endsWith("ing")) {
            suffix = "indë"; stem = stem.substring(0, stem.length() - 3);
        } else if (stem.endsWith("tion") || stem.endsWith("sion")) {
            suffix = "ndor"; stem = stem.substring(0, stem.length() - 4);
        } else if (stem.endsWith("ness")) {
            suffix = "nessë"; stem = stem.substring(0, stem.length() - 4);
        } else if (stem.endsWith("ment")) {
            suffix = "mentë"; stem = stem.substring(0, stem.length() - 4);
        } else if (stem.endsWith("less")) {
            suffix = "lá"; stem = stem.substring(0, stem.length() - 4);
        } else if (stem.endsWith("ful")) {
            suffix = "rima"; stem = stem.substring(0, stem.length() - 3);
        } else if (stem.endsWith("able") || stem.endsWith("ible")) {
            suffix = "ima"; stem = stem.substring(0, stem.length() - 4);
        } else if (stem.endsWith("ory")) {
            suffix = "ondor"; stem = stem.substring(0, stem.length() - 3);
        } else if (stem.endsWith("ary")) {
            suffix = "arwa"; stem = stem.substring(0, stem.length() - 3);
        } else if (stem.endsWith("ic")) {
            suffix = "iquë"; stem = stem.substring(0, stem.length() - 2);
        } else if (stem.endsWith("al")) {
            suffix = "ala"; stem = stem.substring(0, stem.length() - 2);
        } else if (stem.endsWith("ed")) {
            suffix = "ë"; stem = stem.substring(0, stem.length() - 2);
        } else if (stem.endsWith("ly")) {
            suffix = "lë"; stem = stem.substring(0, stem.length() - 2);
        } else if (stem.endsWith("er")) {
            suffix = "or"; stem = stem.substring(0, stem.length() - 2);
        } else if (stem.endsWith("est")) {
            suffix = "esta"; stem = stem.substring(0, stem.length() - 3);
        } else {
            // Añadir ë si termina en consonante
            char last = stem.isEmpty() ? 'a' : stem.charAt(stem.length() - 1);
            suffix = "bcdfghjklmnpqrstvwxyz".indexOf(last) >= 0 ? "ë" : "";
        }

        // Sustituciones fonéticas
        stem = stem.replace("ck", "c")
                   .replace("ph", "f")
                   .replace("wh", "v")
                   .replace("qu", "qu")
                   .replace("w", "v")
                   .replace("j", "y")
                   .replace("x", "cs")
                   .replace("oo", "ú")
                   .replace("ee", "í")
                   .replace("ou", "ó")
                   .replace("th", "th"); // ya suena élfico

        return stem + suffix;
    }
}
