package backend.service;

import java.util.*;

/**
 * Centralised keyword dictionary for complaint severity scoring.
 * Each complaint category maps to a flat list of natural-language phrases
 * (English + Tagalog / Taglish) that a resident might type on mobile.
 *
 * The lists are used for TWO purposes:
 *   1. Exact-match on the complaintType dropdown value.
 *   2. Keyword scan against the free-text description field.
 */
public final class ComplaintKeywords {

    private ComplaintKeywords() {}

    /* ================================================================
     *  TIER 1 – CRITICAL  (base +50)
     * ================================================================ */

    public static final List<String> FIRE = List.of(
        // --- dropdown values ---
        "Fire", "Sunog",
        // --- English ---
        "fire", "burning", "smoke", "flames", "fire risk",
        "caught fire", "on fire", "house fire", "fire broke out",
        "open burning", "fire hazard", "combustion", "blaze",
        "arson", "bonfire", "wildfire", "brush fire",
        "smoke coming from", "structure fire", "flame visible",
        "fire spreading", "fire nearby", "about to catch fire",
        "firestarted", "fyre", "fir hazard",
        // --- Tagalog / Taglish ---
        "sunog", "nasusunog", "nasunog", "nagniningas",
        "may apoy", "usok", "naglalagablab", "apoy",
        "mapanganib na apoy", "nagliliyab", "sinusunog",
        "nag-aapoy", "may sunog", "sumusunog",
        "nagsusunog", "nasunugan", "masusunog",
        "may usok", "umuusok", "naiinitan",
        "sinunog", "pagsusunog", "nagsunog",
        "fire sa bahay", "may fire", "fire dito"
    );

    public static final List<String> ELECTRICAL_HAZARD = List.of(
        "Electrical Hazard", "Delikadong Kuryente",
        "electrical hazard", "exposed wire", "live wire",
        "electric shock", "sparking", "short circuit",
        "power line down", "electrocution risk",
        "faulty wiring", "electrical fire risk",
        "wire hanging", "busted transformer",
        "brown out", "brownout", "black out",
        "nakuryente", "delikadong kuryente", "kuryente",
        "nakakuryente", "may spark", "sunog sa wire",
        "nagliliyab ang wire", "putol na linya",
        "nakabitin na wire", "may kuryente sa tubig",
        "delikado ang kuryente", "sirang transformer",
        "walang kuryente", "paputok ang wire"
    );

    public static final List<String> ANIMAL_BITE = List.of(
        "Animal Bite", "Kagat ng Hayop",
        "animal bite", "dog bite", "bit by dog",
        "bitten", "attacked by dog", "scratched by cat",
        "dog attacked me", "got bitten", "bite from dog",
        "cat scratch", "animal attack", "rabies exposure",
        "dog bit my child", "stray dog bit",
        "snake bite", "snakebite", "rat bite",
        "monkey bite", "bit me", "dog attak",
        "kagat", "kinagat", "nakagat", "nakakagat",
        "kagatin", "pinagkagatan", "kagat ng aso",
        "kinagat ng aso", "kinagat ng pusa",
        "tinuklaw", "kinalmot", "inatake",
        "sinagasaan ng aso", "nakagat ako",
        "may kagat", "nagkagat", "pagkakagat",
        "kagatan", "kinakagat", "nagbite sakin",
        "kinagat ng hayop", "kagat ng pusa",
        "may tuklaw", "tinuklaw ng ahas"
    );

    public static final List<String> MISSING_PERSON = List.of(
        "Missing Person", "Nawawalang Tao",
        "missing person", "missing child", "lost child",
        "someone is missing", "cant find my child",
        "disappeared", "person missing", "missing elderly",
        "missing senior", "lost person", "haven't come home",
        "not home yet", "missing teenager", "runaway",
        "nawawala", "nawawalang tao", "nawawalang bata",
        "nawala ang anak ko", "di pa umuuwi",
        "hindi pa umuuwi", "nawawalang matanda",
        "hinahanap ko", "nawala", "missing ang anak ko",
        "nawawalang teenager", "walang balita",
        "di pa nag-uuwi", "nawawalang asawa"
    );

    public static final List<String> HARASSMENT = List.of(
        "Harassment", "Pananakot o Pang-aabuso",
        "harassment", "threatened", "harassed", "bullied",
        "stalked", "intimidated", "verbal abuse",
        "death threat", "threat", "stalking",
        "sexual harassment", "cyberbullying", "blackmail",
        "extortion", "harrased", "harasment", "bullying",
        "being followed", "someone threatening me",
        "nananakot", "tinatakot", "inabuso", "sinusundan",
        "tinutukso", "minumura", "inaaway",
        "pananakot", "pang-aabuso", "binubully",
        "hinaharas", "tinatatakot", "pinagtatakotan",
        "nang-aabuso", "binabanta", "banta",
        "ginagabay", "nambubully", "minamura",
        "may nagbabanta", "may nananakot",
        "pinagbabantaan", "nang-aalipusta"
    );

    /* ================================================================
     *  TIER 2 – HIGH  (base +30)
     * ================================================================ */

    public static final List<String> DOMESTIC_CONFLICT = List.of(
        "Domestic Conflict", "Away sa Tahanan",
        "domestic conflict", "domestic violence", "fight at home",
        "family fight", "husband hitting", "wife beating",
        "hitting spouse", "assault at home", "domestic abuse",
        "argument at home", "fighting", "altercation",
        "beating", "physical abuse", "slapping",
        "away", "nag-away", "nag-aaway", "palo", "pinalo",
        "suntok", "sinuntok", "nagsusuntukan",
        "away sa bahay", "kagalitan", "sigawan",
        "bunguan", "away mag-asawa", "away sa tahanan",
        "pinagsusuntukan", "binubugbog", "bugbugan",
        "sinapak", "sampal", "sinampal",
        "nag-aaway sa bahay", "away sa pamilya",
        "nagwawala sa bahay", "nagkakagulatan"
    );

    public static final List<String> THEFT = List.of(
        "Theft", "Pagnanakaw",
        "theft", "stolen", "robbery", "robbed",
        "pickpocket", "snatching", "burglar", "break in",
        "burglary", "hold up", "holdup", "mugging",
        "shoplifting", "carjacking", "bike stolen",
        "phone stolen", "bag snatched", "break-in",
        "stole my", "someone stole", "got robbed",
        "nakaw", "ninakaw", "nanakaw", "magnanakaw",
        "holdap", "snatcher", "snatched",
        "nawala ang gamit", "ninakawan",
        "hinoldap", "nahold-up", "nanakawan",
        "tinangay", "kinuha", "inagaw",
        "may nagnakaw", "nakawan", "pagnanakaw",
        "may holdaper", "na-snatch", "hinablot"
    );

    public static final List<String> SUSPICIOUS_ACTIVITY = List.of(
        "Suspicious Activity", "Kahina-hinalang Gawain",
        "suspicious", "lurking", "loitering",
        "acting weird", "sketchy person", "strange behavior",
        "suspicious person", "peeping tom", "prowler",
        "casing the area", "unknown person", "stranger lurking",
        "watching houses", "suspicious vehicle",
        "kahina-hinala", "kahina-hinalang tao",
        "nagpupumilit", "nagtatago", "nagmamasid",
        "palasingasing", "kahinahinala",
        "may nagmamasid", "may nakatambay",
        "nag-iikot ikot", "nagbabantay",
        "hindi kilala", "may nangangalkal",
        "may nagsusundot", "may kumakatok gabi"
    );

    public static final List<String> PUBLIC_HEALTH = List.of(
        "Public Health Concern", "Banta sa Kalusugan",
        "public health", "health hazard", "disease outbreak",
        "dengue", "contaminated water", "unsanitary",
        "health concern", "epidemic", "infection spread",
        "food poisoning", "health risk", "mosquito breeding",
        "lamok", "dengue", "sakit", "epidemya",
        "banta sa kalusugan", "marumi ang tubig",
        "may sakit", "nagkakalat na sakit",
        "kontaminado", "hindi malinis", "maruming tubig",
        "may lamok", "breeding ground"
    );

    public static final List<String> INFRASTRUCTURE_DAMAGE = List.of(
        "Infrastructure Damage", "Sira na Imprastraktura",
        "infrastructure damage", "broken bridge",
        "collapsed wall", "cracked pavement",
        "damaged structure", "structural damage",
        "road collapsed", "sinkhole", "guardrail broken",
        "sira", "sira na imprastraktura", "gumuho",
        "bumagsak", "nasira ang tulay", "wasak",
        "pinsala sa gusali", "basag", "gumuguho"
    );

    public static final List<String> FALLEN_TREE = List.of(
        "Fallen Tree", "Nabuwal na Puno",
        "fallen tree", "tree fell", "tree down",
        "tree blocking road", "uprooted tree",
        "broken branch", "tree obstruction",
        "dangerous tree", "leaning tree",
        "nabuwal na puno", "nahulog na puno",
        "bumagsak na puno", "hadlang na puno",
        "puno sa kalsada", "natumba ang puno",
        "nabali ang sanga", "may nabuwal na puno"
    );

    /* ================================================================
     *  TIER 3 – MODERATE  (base +15)
     * ================================================================ */

    public static final List<String> VANDALISM = List.of(
        "Vandalism", "Pag-vandal sa Ari-arian",
        "vandalism", "graffiti", "property damage",
        "destroyed property", "smashed window",
        "broken window", "defaced wall", "spray paint",
        "vandalized", "wrecked", "damaged fence",
        "vandalismo", "binasag", "sinira",
        "dinumihan", "pinsala sa ari-arian",
        "siniraan", "may nag-vandal", "graffiti sa pader"
    );

    public static final List<String> TRESPASSING = List.of(
        "Trespassing", "Pagpasok nang Walang Pahintulot",
        "trespassing", "trespasser", "unauthorized entry",
        "intruder", "break in", "entered property",
        "someone broke in", "forced entry",
        "pumasok sa lupa", "pumasok sa bahay",
        "walang pahintulot", "nanghimasok",
        "may pumasok", "pinasok ang bahay"
    );

    public static final List<String> WATER_LEAKAGE = List.of(
        "Water Leakage", "Tumagas na Tubo",
        "water leak", "water leakage", "pipe burst",
        "broken pipe", "leaking pipe", "pipe issue",
        "water main break", "dripping water", "busted pipe",
        "tumagas", "tumagas na tubo", "may tulo",
        "basag ang tubo", "tumutulo", "sira ang tubo",
        "may butas ang tubo", "pagkasira ng tubo",
        "biyak na tubo", "may leak"
    );

    public static final List<String> FLOODING = List.of(
        "Flooding", "Baha",
        "flood", "flooded", "water rising",
        "flash flood", "overflow", "waterlogged",
        "flooding", "submerged", "water level rising",
        "flooded street", "knee deep water",
        "baha", "bumabaha", "nabaha", "nagbabaha",
        "binabaha", "mababaw na baha", "umaapaw",
        "tubig sa kalsada", "lumubog", "lubog sa baha",
        "mataas ang tubig", "may baha",
        "bahain", "binabaha kami", "flash flood"
    );

    public static final List<String> CLOGGED_CANAL = List.of(
        "Clogged Canal", "Baradong Kanal",
        "clogged canal", "clogged drain", "blocked sewer",
        "drainage blocked", "backed up drain",
        "clogged sewer", "drain overflow",
        "barado", "baradong kanal", "baradong drain",
        "hindi dumadaloy", "umaapaw ang kanal",
        "sira ang drainage", "puno ang canal",
        "hindi umaagos", "baradong imburnal"
    );

    public static final List<String> ROAD_DAMAGE = List.of(
        "Road Damage", "Sira na Kalsada",
        "road damage", "pothole", "broken road",
        "cracked road", "uneven road", "road hazard",
        "dangerous road", "road needs repair",
        "lubak", "sira na kalsada", "butas sa kalsada",
        "wasak na daan", "lubak lubak",
        "pinsala sa kalsada", "delikadong kalsada",
        "sira ang daan", "may butas sa daan"
    );

    public static final List<String> BROKEN_STREETLIGHT = List.of(
        "Broken Streetlight", "Sirang Ilaw sa Kalsada",
        "broken streetlight", "street light out",
        "no street light", "busted lamp post",
        "dark street", "streetlight not working",
        "lamp post broken", "light pole down",
        "sirang ilaw", "sirang ilaw sa kalsada",
        "walang ilaw", "sira ang poste",
        "madilim", "walang ilaw sa kalye",
        "sira ang street light", "patay ang ilaw"
    );

    public static final List<String> ILLEGAL_CONSTRUCTION = List.of(
        "Illegal Construction", "Itinayo nang Walang Permit",
        "illegal construction", "no building permit",
        "unauthorized building", "illegal structure",
        "building without permit", "unpermitted construction",
        "walang permit", "itinayo nang walang permit",
        "iligal na konstruksyon", "walang pahintulot",
        "illegal na gusali", "nagtayo ng walang permit"
    );

    public static final List<String> BUILDING_CODE = List.of(
        "Building Code Violation", "Paglabag sa Kodigo ng Gusali",
        "building code violation", "code violation",
        "structural violation", "unsafe building",
        "building violation", "fire code violation",
        "paglabag sa kodigo", "labag sa building code",
        "hindi pasado sa code", "delikadong gusali"
    );

    public static final List<String> POLLUTION = List.of(
        "Pollution", "Polusyon",
        "pollution", "air pollution", "water pollution",
        "noise pollution", "toxic waste", "chemical spill",
        "fumes", "smog", "contamination",
        "polusyon", "maruming hangin", "maruming tubig",
        "kontaminasyon", "amoy kemikal", "dumi sa ilog",
        "may lason sa tubig", "masamang hangin"
    );

    public static final List<String> ABANDONED_VEHICLE = List.of(
        "Abandoned Vehicle", "Inabandunang Sasakyan",
        "abandoned vehicle", "abandoned car",
        "abandoned truck", "junk vehicle",
        "derelict car", "left vehicle", "unattended vehicle",
        "inabandunang sasakyan", "naiwang sasakyan",
        "iniwan na kotse", "nakatambay na sasakyan",
        "walang may-ari na kotse", "abandoned na kotse"
    );

    public static final List<String> ILLEGAL_VENDOR = List.of(
        "Illegal Vendor", "Iligal na Nagtitinda",
        "illegal vendor", "sidewalk vendor",
        "unlicensed seller", "street vendor blocking",
        "obstruction by vendor", "illegal stall",
        "iligal na nagtitinda", "nagtitinda sa kalye",
        "walang permit na tindahan", "hadlang sa daan",
        "nagtitinda sa sidewalk", "ambulant vendor"
    );

    /* ================================================================
     *  TIER 4 – LOW  (base +5)
     * ================================================================ */

    public static final List<String> NEIGHBORHOOD_DISPUTE = List.of(
        "Neighborhood Dispute", "Alitan sa Kapitbahay",
        "neighborhood dispute", "neighbor fight",
        "neighbor conflict", "neighbor argument",
        "dispute with neighbor", "property line dispute",
        "boundary dispute", "neighborly disagreement",
        "alitan sa kapitbahay", "away sa kapitbahay",
        "bangayan", "nag-aaway na kapitbahay",
        "problema sa kapitbahay", "sigawan ng kapitbahay"
    );

    public static final List<String> NOISE_COMPLAINT = List.of(
        "Noise Complaint", "Reklamo sa Ingay",
        "noise complaint", "loud noise", "noisy",
        "disturbing noise", "music too loud",
        "shouting", "yelling", "loud music",
        "noise at night", "barking dog",
        "too loud", "can't sleep noise",
        "ingay", "maingay", "malakas", "magulo",
        "nakaka-ingay", "malakas ang tugtog",
        "sigaw", "sumisigaw", "nakakaingay",
        "sobrang ingay", "makulit na tugtog",
        "maingay na kapitbahay", "ingay sa gabi"
    );

    public static final List<String> BUSINESS_NOISE = List.of(
        "Business Noise", "Ingay Mula sa Negosyo",
        "business noise", "noisy business",
        "loud establishment", "bar noise",
        "club noise", "noisy store",
        "ingay mula sa negosyo", "maingay na negosyo",
        "maingay na bar", "maingay na tindahan",
        "maingay na establishment"
    );

    public static final List<String> PUBLIC_DISTURBANCE = List.of(
        "Public Disturbance", "Kaguluhan sa Publiko",
        "public disturbance", "disturbance",
        "commotion", "scene", "ruckus",
        "disorderly conduct", "public nuisance",
        "kaguluhan", "kaguluhan sa publiko",
        "gulo", "nagwawala", "may nagwawala",
        "nagkakagulo", "ingay sa labas",
        "gulong gulo", "may gulo sa labas"
    );

    public static final List<String> ILLEGAL_PARKING = List.of(
        "Illegal Parking", "Iligal na Pagpapark",
        "illegal parking", "double parking",
        "blocking driveway", "no parking zone",
        "parked illegally", "obstruction",
        "iligal na pagpapark", "iligal na parking",
        "harang sa daan", "naka-park sa bawal",
        "nakaharang", "naka-double park",
        "nakaharang ang kotse"
    );

    public static final List<String> PARKING_DISPUTE = List.of(
        "Parking Dispute", "Alitan sa Parking",
        "parking dispute", "parking fight",
        "parking argument", "parking conflict",
        "alitan sa parking", "away sa parking",
        "pinag-aawayan ang parking"
    );

    public static final List<String> GARBAGE_ISSUE = List.of(
        "Garbage Issue", "Problema sa Basura",
        "garbage", "trash", "dumping", "littering",
        "illegal dumping", "waste pile", "garbage pile",
        "overflowing trash", "garbage everywhere",
        "trash not collected", "dirty area",
        "basura", "nagtatapon", "nagkalat",
        "dumi", "tambak ng basura", "basurero",
        "masamang amoy", "nagtatapon ng basura",
        "kalat", "marumi", "nagkalat ang basura",
        "puno ang basurahan", "hindi kinukuha ang basura",
        "mabaho", "may nagkalat na basura"
    );

    public static final List<String> SANITATION = List.of(
        "Sanitation Problem", "Problema sa Kalinisan",
        "sanitation problem", "unsanitary conditions",
        "dirty area", "sewage problem", "sewage leak",
        "filthy", "unhygienic", "sanitation issue",
        "problema sa kalinisan", "marumi",
        "hindi malinis", "mabaho", "maruming lugar",
        "walang kalinisan", "may dumi"
    );

    public static final List<String> ANIMAL_CONCERN = List.of(
        "Animal Concern", "Alalahanin sa Hayop",
        "animal concern", "animal welfare",
        "injured animal", "sick animal",
        "animal cruelty", "dead animal",
        "alalahanin sa hayop", "sugatan na hayop",
        "may patay na hayop", "pinapatay ang hayop",
        "inaabuso ang hayop", "maysakit na hayop"
    );

    public static final List<String> STRAY_ANIMALS = List.of(
        "Stray Animals", "Ligaw na Hayop",
        "stray dog", "stray cat", "wandering animal",
        "loose dog", "roaming animals", "stray animals",
        "feral cat", "wild dog", "unleashed dog",
        "ligaw na aso", "ligaw na pusa",
        "gala", "galang hayop", "nakakalat na hayop",
        "asong gala", "pusang gala",
        "asong ligaw", "asong kalye",
        "nakakalat na aso", "may gala na aso"
    );

    public static final List<String> CURFEW_VIOLATION = List.of(
        "Curfew Violation", "Paglabag sa Oras ng Pagbabawal",
        "curfew violation", "breaking curfew",
        "out past curfew", "curfew breaker",
        "paglabag sa curfew", "lumabag sa curfew",
        "labag sa oras", "gala ng gabi",
        "lumalabag sa curfew", "walang curfew pass"
    );

    public static final List<String> ORDINANCE_VIOLATION = List.of(
        "Ordinance Violation", "Paglabag sa Batas ng Barangay",
        "ordinance violation", "barangay ordinance",
        "violating ordinance", "local law violation",
        "paglabag sa ordinansa", "labag sa batas",
        "paglabag sa batas ng barangay",
        "lumabag sa ordinansa"
    );

    public static final List<String> LOST_AND_FOUND = List.of(
        "Lost and Found", "Nawala o Natagpuan",
        "lost and found", "lost item", "found item",
        "lost wallet", "lost phone", "found something",
        "missing item", "lost keys",
        "nawala", "natagpuan", "nawala ang gamit",
        "may nakita", "nawala ang wallet",
        "nawala ang phone", "may napulot",
        "nawalan ng gamit", "naiwala ko"
    );

    public static final List<String> OTHER = List.of(
        "Other", "Iba pa",
        "other", "iba pa", "hindi ko alam",
        "general concern", "other concern",
        "iba pang reklamo"
    );

    /* ================================================================
     *  TIER ASSIGNMENT – maps each keyword list to its severity tier.
     * ================================================================ */

    /** tier → keyword lists. Used by SeverityService. */
    public static final Map<Integer, List<List<String>>> TIERS;

    static {
        Map<Integer, List<List<String>>> m = new LinkedHashMap<>();

        m.put(50, List.of(
            FIRE, ELECTRICAL_HAZARD, ANIMAL_BITE,
            MISSING_PERSON, HARASSMENT
        ));

        m.put(30, List.of(
            DOMESTIC_CONFLICT, THEFT, SUSPICIOUS_ACTIVITY,
            PUBLIC_HEALTH, INFRASTRUCTURE_DAMAGE, FALLEN_TREE
        ));

        m.put(15, List.of(
            VANDALISM, TRESPASSING, WATER_LEAKAGE,
            FLOODING, CLOGGED_CANAL, ROAD_DAMAGE,
            BROKEN_STREETLIGHT, ILLEGAL_CONSTRUCTION,
            BUILDING_CODE, POLLUTION, ABANDONED_VEHICLE,
            ILLEGAL_VENDOR
        ));

        m.put(5, List.of(
            NEIGHBORHOOD_DISPUTE, NOISE_COMPLAINT,
            BUSINESS_NOISE, PUBLIC_DISTURBANCE,
            ILLEGAL_PARKING, PARKING_DISPUTE,
            GARBAGE_ISSUE, SANITATION, ANIMAL_CONCERN,
            STRAY_ANIMALS, CURFEW_VIOLATION,
            ORDINANCE_VIOLATION, LOST_AND_FOUND, OTHER
        ));

        TIERS = Collections.unmodifiableMap(m);
    }
}
