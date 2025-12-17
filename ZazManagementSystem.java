import component.*;
import component.Character;
import db.*;
import managment.ManagementSystem;
import search.SearchHistory;
import repository.*;
import repository.jdbc.*;
import service.*;
import filter.*;
import builder.CharacterBuilder;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

public class ZazManagementSystem {
    private DatabaseConnectionProvider provider;
    private DatabaseManager dbManager;
    private Scanner scanner;
    private User currentUser;
    private SearchHistory searchHistory;
    private ManagementSystem facade;

    public ZazManagementSystem() {
        provider = new SQLiteConnection();
        dbManager = new DatabaseManager(provider);
        scanner = new Scanner(System.in);
        searchHistory = new SearchHistory();
        initializeFacade();
    }

    private void initializeFacade() {
        Connection conn = dbManager.getConnection();

        CharacterRepository charRepo = new JdbcCharacterRepository(conn);
        MediaRepository mediaRepo = new JdbcMediaRepository(conn, charRepo);
        UserRepository userRepo = new JdbcUserRepository(conn);
        FavoriteRepository favRepo = new JdbcFavoriteRepository(conn, charRepo);

        MediaService mediaService = new MediaService(mediaRepo, charRepo);
        CharacterService characterService = new CharacterService(charRepo);
        UserService userService = new UserService(userRepo);
        FavoriteService favoriteService = new FavoriteService(favRepo);

        facade = new ManagementSystem(mediaService, characterService, userService, favoriteService);
    }

    public void start() {
        System.out.println("\n" +
                "                                                     \n" +
                " ██████████████████ ██████████████ ██████████████████\n" +
                " ██░░░░░░░░░░░░░░██ ██░░░░░░░░░░██ ██░░░░░░░░░░░░░░██\n" +
                " ████████████░░░░██ ██░░██████░░██ ████████████░░░░██\n" +
                "         ████░░████ ██░░██  ██░░██         ████░░████\n" +
                "       ████░░████   ██░░██████░░██       ████░░████\n" +
                "     ████░░████     ██░░░░░░░░░░██     ████░░████ \n" +
                "   ████░░████       ██░░██████░░██   ████░░████\n" +
                " ████░░████         ██░░██  ██░░██ ████░░████         \n" +
                " ██░░░░████████████ ██░░██  ██░░██ ██░░░░████████████ \n" +
                " ██░░░░░░░░░░░░░░██ ██░░██  ██░░██ ██░░░░░░░░░░░░░░██ \n" +
                " ██████████████████ ██████  ██████ ██████████████████\n" +
                "                                                        ");

        while (true) {
            System.out.println("\n1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("\nChoose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    if (login()) {
                        if (currentUser.isAdmin()) {
                            adminMenu();
                        } else {
                            userMenu();
                        }
                    }
                }
                case 2 -> register();
                case 3 -> {
                    dbManager.close();
                    System.out.println("Goodbye!");
                    return;
                }
            }
        }
    }

    private boolean login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentUser = facade.login(username, password);
        if (currentUser != null) {
            System.out.println("Login successful! Welcome " + username + "!");
            return true;
        } else {
            System.out.println("Invalid credentials!");
            return false;
        }
    }

    private void register() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentUser = facade.register(username, email, password);
        if (currentUser != null) {
            System.out.println("Registration successful! Welcome " + username + "!");
            userMenu();
        }
    }

    private void userMenu() {
        while (true) {
            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ User Menu ▀▄▀▄▀▄▀▄▀▄▀▄");
            System.out.println("1. Profile");
            System.out.println("2. Create media");
            System.out.println("3. Create character");
            System.out.println("4. Browse media");
            System.out.println("5. Browse characters");
            System.out.println("6. Search");
            System.out.println("7. Logout");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> showProfile();
                case 2 -> createMedia();
                case 3 -> createCharacter();
                case 4 -> browseMedia();
                case 5 -> browseCharacters();
                case 6 -> search();
                case 7 -> { return; }
            }
        }
    }

    private void adminMenu() {
        while (true) {
            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Admin Menu ▀▄▀▄▀▄▀▄▀▄▀▄");
            System.out.println("1. Profile");
            System.out.println("2. Create media");
            System.out.println("3. Create character");
            System.out.println("4. Manage content");
            System.out.println("5. Manage users");
            System.out.println("6. Browse media");
            System.out.println("7. Browse characters");
            System.out.println("8. Search");
            System.out.println("9. Logout");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> showProfile();
                case 2 -> createMedia();
                case 3 -> createCharacter();
                case 4 -> manageContent();
                case 5 -> manageUsers();
                case 6 -> browseMedia();
                case 7 -> browseCharacters();
                case 8 -> search();
                case 9 -> { return; }
            }
        }
    }

    private void showProfile() {
        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Profile ▀▄▀▄▀▄▀▄▀▄▀▄");
        System.out.println("Username: " + currentUser.getUsername());
        System.out.println("Email: " + currentUser.getEmail());

        if (!currentUser.isAdmin()) {
            System.out.println("\n1. My favorites");
            System.out.println("2. My media");
            System.out.println("3. My characters");
            System.out.println("4. Go back");
        } else {
            System.out.println("\n1. My media");
            System.out.println("2. My characters");
            System.out.println("3. Go back");
        }

        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (!currentUser.isAdmin()) {
            switch (choice) {
                case 1 -> showFavorites();
                case 2 -> showMyMedia();
                case 3 -> showMyCharacters();
            }
        } else {
            switch (choice) {
                case 1 -> showMyMedia();
                case 2 -> showMyCharacters();
            }
        }
    }

    private void showFavorites() {
        System.out.println("\n1. Favorite Media");
        System.out.println("2. Favorite Characters");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            showFavoriteMedia();
        } else if (choice == 2) {
            showFavoriteCharacters();
        }
    }

    private void showFavoriteMedia() {
        List<Media> favorites = facade.getFavoriteMedia(currentUser.getId());

        if (favorites.isEmpty()) {
            System.out.println("No favorite media.");
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Favorite Media ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < favorites.size(); i++) {
            System.out.println(i + 1 + ". " + favorites.get(i).getTitle() + " | " + favorites.get(i).getType());
        }

        System.out.print("\nChoose media to remove (0 to go back): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= favorites.size()) {
            facade.removeFromFavorites(currentUser.getId(), favorites.get(choice - 1).getId(), true);
        } else {
            showProfile();
        }
    }

    private void showFavoriteCharacters() {
        List<Character> favorites = facade.getFavoriteCharacters(currentUser.getId());

        if (favorites.isEmpty()) {
            System.out.println("No favorite characters.");
            return;
        }

        System.out.println("\n=▀▄▀▄▀▄▀▄▀▄▀▄ Favorite Characters ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < favorites.size(); i++) {
            System.out.println(i + 1 + ". " + favorites.get(i).getName());
        }

        System.out.print("\nChoose character to remove (0 to go back): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= favorites.size()) {
            facade.removeFromFavorites(currentUser.getId(), favorites.get(choice - 1).getId(), false);
        } else {
            showProfile();
        }
    }

    private void showMyMedia() {
        List<Media> myMedia = facade.getUserMedia(currentUser.getId());

        if (myMedia.isEmpty()) {
            System.out.println("You haven't created any media yet.");
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ My Media ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < myMedia.size(); i++) {
            System.out.println(i + 1 + ". " + myMedia.get(i).getTitle() + " | " + myMedia.get(i).getType());
        }

        System.out.print("\nChoose media to delete (0 to go back): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= myMedia.size()) {
            facade.deleteMedia(myMedia.get(choice - 1).getId());
            System.out.println("Media deleted successfully!");
        } else {
            showProfile();
        }
    }

    private void showMyCharacters() {
        List<Character> myCharacters = facade.getUserCharacters(currentUser.getId());

        if (myCharacters.isEmpty()) {
            System.out.println("You haven't created any characters yet.");
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ My Characters ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < myCharacters.size(); i++) {
            System.out.println((i + 1) + ". " + myCharacters.get(i).getName());
        }

        System.out.print("\nChoose character to delete (0 to go back): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= myCharacters.size()) {
            facade.deleteCharacter(myCharacters.get(choice - 1).getId());
            System.out.println("Character deleted successfully!");
        } else {
            showProfile();
        }
    }

    private void createMedia() {
        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.print("Description: ");
        String description = scanner.nextLine();

        System.out.print("Release Date (YYYY-MM-DD): ");
        String releaseDate = scanner.nextLine();

        System.out.println("\nAvailable types:");
        for (int i = 0; i < MediaType.TYPES.length; i++) {
            System.out.println(i + 1 + ". " + MediaType.TYPES[i]);
        }
        System.out.print("Choose type: ");
        int typeChoice = scanner.nextInt();
        scanner.nextLine();
        String type = (typeChoice > 0 && typeChoice <= MediaType.TYPES.length) ? MediaType.TYPES[typeChoice - 1] : "OTHER";

        System.out.println("\nAvailable genres:");
        for (int i = 0; i < MediaGenre.GENRES.length; i++) {
            System.out.println(i + 1 + ". " + MediaGenre.GENRES[i]);
        }
        System.out.print("Choose genre: ");
        int genreChoice = scanner.nextInt();
        scanner.nextLine();
        String genre = (genreChoice > 0 && genreChoice <= MediaGenre.GENRES.length) ? MediaGenre.GENRES[genreChoice - 1] : "Other";

        System.out.print("Hashtags (comma-separated): ");
        String hashtags = scanner.nextLine();

        facade.createMedia(title, description, releaseDate, type, genre, hashtags, currentUser.getId());
        System.out.println("Media created successfully!");
    }

    private void createCharacter() {
        List<Media> mediaList = facade.browseMedia(items -> items);
        if (mediaList.isEmpty()) {
            System.out.println("No media available. Create media first!");
            return;
        }

        System.out.println("\nAvailable Media:");
        for (int i = 0; i < mediaList.size(); i++) {
            System.out.println(i + 1 + ". " + mediaList.get(i).getTitle());
        }

        System.out.print("Choose media number: ");
        int mediaChoice = scanner.nextInt();
        scanner.nextLine();

        if (mediaChoice < 1 || mediaChoice > mediaList.size()) {
            System.out.println("Invalid choice!");
            return;
        }

        Media selectedMedia = mediaList.get(mediaChoice - 1);
        CharacterBuilder builder = new CharacterBuilder();

        System.out.print("Character Name: ");
        builder.setName(scanner.nextLine());

        System.out.print("Age: ");
        builder.setAge(scanner.nextInt());
        scanner.nextLine();

        System.out.print("Birthday (YYYY-MM-DD): ");
        builder.setBirthday(scanner.nextLine());

        System.out.print("Gender: ");
        builder.setGender(scanner.nextLine());

        System.out.print("Species: ");
        builder.setSpecies(scanner.nextLine());

        System.out.print("Activity: ");
        builder.setActivity(scanner.nextLine());

        System.out.print("Add additional fields? (yes/no): ");
        String addFields = scanner.nextLine();

        if (addFields.equalsIgnoreCase("yes")) {
            while (true) {
                System.out.print("Field name (0 to finish): ");
                String fieldName = scanner.nextLine();
                if (fieldName.equals("0")) break;

                System.out.print("Field value: ");
                String fieldValue = scanner.nextLine();
                builder.addAdditionalField(fieldName, fieldValue);
            }
        }

        System.out.print("Description: ");
        builder.setDescription(scanner.nextLine());

        System.out.print("Hashtags (comma-separated): ");
        builder.setHashtags(scanner.nextLine());

        builder.setMediaId(selectedMedia.getId());

        facade.createCharacter(builder);
        System.out.println("Character created successfully!");
    }

    private void browseMedia() {
        FilterComposite<Media> filter = new FilterComposite<>();
        List<Media> mediaList = facade.browseMedia(filter);
        browseMediaWithFilter(mediaList, filter);
    }

    private void browseMediaWithFilter(List<Media> mediaList, FilterComposite<Media> currentFilter) {
        if (mediaList.isEmpty()) {
            System.out.println("No media available.");
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ All Media ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < mediaList.size(); i++) {
            System.out.print((i + 1) + ". ");
            mediaList.get(i).display();
        }

        System.out.println("\n1. Show details");
        System.out.println("2. Edit filters");
        System.out.println("3. Go back to menu");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.print("Choose media number: ");
                int mediaChoice = scanner.nextInt();
                scanner.nextLine();
                if (mediaChoice > 0 && mediaChoice <= mediaList.size()) {
                    showMediaDetails(mediaList.get(mediaChoice - 1));
                }
            }
            case 2 -> editMediaFilters(currentFilter);
        }
    }

    private void showMediaDetails(Media media) {
        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Media Details ▀▄▀▄▀▄▀▄▀▄▀▄");
        System.out.println(media.getDetails());

        String authorName = facade.getMediaAuthorName(media.getId());
        int authorId = facade.getMediaAuthorId(media.getId());
        System.out.println("Author: " + authorName);

        System.out.println("\n1. Add to favorites");
        System.out.println("2. Show all characters");
        System.out.println("3. View author's profile");
        System.out.println("4. Go back");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                facade.addToFavorites(currentUser.getId(), media.getId(), true);
                showMediaDetails(media);
            }
            case 2 -> {
                showMediaCharacters(media);
                showMediaDetails(media);
            }
            case 3 -> viewAuthorProfile(authorId, authorName);
            case 4 -> browseMedia();
        }
    }

    private void showMediaCharacters(Media media) {
        if (media.getCharacters().isEmpty()) {
            System.out.println("No characters in this media.");
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Characters ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < media.getCharacters().size(); i++) {
            System.out.println(i + 1 + ". " + media.getCharacters().get(i).getName());
        }

        System.out.print("\nView character details (0 to go back): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= media.getCharacters().size()) {
            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Character Details ▀▄▀▄▀▄▀▄▀▄▀▄");
            System.out.println(media.getCharacters().get(choice - 1).getDetails());
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private void editMediaFilters(FilterComposite<Media> currentFilter) {
        FilterComposite<Media> newFilter = new FilterComposite<>();
        Map<String, String> activeFilters = new HashMap<>();

        while (true) {
            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Filters ▀▄▀▄▀▄▀▄▀▄▀▄");
            System.out.println("1. Type: " + activeFilters.getOrDefault("Type", ""));
            System.out.println("2. Genre: " + activeFilters.getOrDefault("Genre", ""));
            System.out.println("3. Release date: " + activeFilters.getOrDefault("Release date", ""));
            System.out.println("4. Hashtag: " + activeFilters.getOrDefault("Hashtag", ""));
            System.out.println("0. Apply filters");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) {
                List<Media> filtered = facade.browseMedia(newFilter);
                browseMediaWithFilter(filtered, newFilter);
                return;
            }

            switch (choice) {
                case 1 -> {
                    System.out.println("\nTypes:");
                    for (int i = 0; i < MediaType.TYPES.length; i++) {
                        System.out.println(i + 1 + ". " + MediaType.TYPES[i]);
                    }
                    System.out.print("Choose types (comma-separated): ");
                    String input = scanner.nextLine();
                    Set<String> types = Arrays.stream(input.split(","))
                            .map(String::trim)
                            .map(Integer::parseInt)
                            .filter(i -> i > 0 && i <= MediaType.TYPES.length)
                            .map(i -> MediaType.TYPES[i - 1])
                            .collect(Collectors.toSet());
                    if (!types.isEmpty()) {
                        newFilter.add(new TypeFilterStrategy(types));
                        activeFilters.put("Type", String.join(", ", types));
                    }
                }
                case 2 -> {
                    System.out.println("\nGenres:");
                    for (int i = 0; i < MediaGenre.GENRES.length; i++) {
                        System.out.println(i + 1 + ". " + MediaGenre.GENRES[i]);
                    }
                    System.out.print("Choose genres (comma-separated): ");
                    String input = scanner.nextLine();
                    Set<String> genres = Arrays.stream(input.split(","))
                            .map(String::trim)
                            .map(Integer::parseInt)
                            .filter(i -> i > 0 && i <= MediaGenre.GENRES.length)
                            .map(i -> MediaGenre.GENRES[i - 1])
                            .collect(Collectors.toSet());
                    if (!genres.isEmpty()) {
                        newFilter.add(new GenreFilterStrategy(genres));
                        activeFilters.put("Genre", String.join(", ", genres));
                    }
                }
                case 3 -> {
                    System.out.print("Enter release date (YYYY or YYYY-MM-DD): ");
                    String date = scanner.nextLine();
                    newFilter.add(new ReleaseDateFilterStrategy(date));
                    activeFilters.put("Release date", String.join(", ", date));
                }
                case 4 -> {
                    System.out.print("Enter hashtag: ");
                    String hashtag = scanner.nextLine();
                    newFilter.add(new HashtagMediaFilterStrategy(hashtag));
                    activeFilters.put("Hashtag", String.join(", ", hashtag));
                }
            }
        }
    }

    private void browseCharacters() {
        FilterComposite<Character> filter = new FilterComposite<>();
        List<Character> characters = facade.browseCharacters(filter);
        browseCharactersWithFilter(characters, filter);
    }

    private void browseCharactersWithFilter(List<Character> characters, FilterComposite<Character> currentFilter) {
        if (characters.isEmpty()) {
            System.out.println("No characters available.");
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ All Characters ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < characters.size(); i++) {
            Media media = facade.viewMediaDetails(characters.get(i).getMediaId());
            System.out.println(i + 1 + ". " + characters.get(i).getName() + " | " + media.getTitle());
        }

        System.out.println("\n1. Show details");
        System.out.println("2. Edit filters");
        System.out.println("3. Go back to menu");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.print("Choose character number: ");
                int charChoice = scanner.nextInt();
                scanner.nextLine();
                if (charChoice > 0 && charChoice <= characters.size()) {
                    showCharacterDetails(characters.get(charChoice - 1));
                }
            }
            case 2 -> editCharacterFilters(currentFilter);
        }
    }

    private void showCharacterDetails(Character character) {
        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Character Details ▀▄▀▄▀▄▀▄▀▄▀▄");
        System.out.println(character.getDetails());

        Media media = facade.viewMediaDetails(character.getMediaId());
        if (media != null) {
            System.out.println("From Media: " + media.getTitle());
        }

        String authorName = facade.getMediaAuthorName(character.getMediaId());
        int authorId = facade.getMediaAuthorId(character.getMediaId());
        System.out.println("Author: " + authorName);

        System.out.println("\n1. Add to favorites");
        System.out.println("2. View author's profile");
        System.out.println("3. Go back");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                facade.addToFavorites(currentUser.getId(), character.getId(), false);
                showCharacterDetails(character);
            }
            case 2 -> viewAuthorProfile(authorId, authorName);
            case 3 -> browseCharacters();
        }
    }

    private void editCharacterFilters(FilterComposite<Character> currentFilter) {
        FilterComposite<Character> newFilter = new FilterComposite<>();
        Map<String, String> activeFilters = new HashMap<>();

        while (true) {
            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Filters ▀▄▀▄▀▄▀▄▀▄▀▄");
            System.out.println("1. Age: " + activeFilters.getOrDefault("Age", ""));
            System.out.println("2. Birthday: " + activeFilters.getOrDefault("Birthday", ""));
            System.out.println("3. Species: " + activeFilters.getOrDefault("Species", ""));
            System.out.println("4. Activity: " + activeFilters.getOrDefault("Activity", ""));
            System.out.println("5. Hashtag: " + activeFilters.getOrDefault("Hashtag", ""));
            System.out.println("0. Apply filters");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) {
                List<Character> filtered = facade.browseCharacters(newFilter);
                browseCharactersWithFilter(filtered, newFilter);
                return;
            }

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter age: ");
                    String age = scanner.nextLine();
                    newFilter.add(new AgeCharacterFilterStrategy(age));
                    activeFilters.put("Age", String.join(", ", age));
                }
                case 2 -> {
                    System.out.print("Enter birthday: ");
                    String birthday = scanner.nextLine();
                    newFilter.add(new BirthdayCharacterFilterStrategy(birthday));
                    activeFilters.put("Birthday", String.join(", ", birthday));
                }
                case 3 -> {
                    System.out.print("Enter species: ");
                    String species = scanner.nextLine();
                    newFilter.add(new SpeciesCharacterFilterStrategy(species));
                    activeFilters.put("Species", String.join(", ", species));
                }
                case 4 -> {
                    System.out.print("Enter activity: ");
                    String activity = scanner.nextLine();
                    newFilter.add(new ActivityCharacterFilterStrategy(activity));
                    activeFilters.put("Activity", String.join(", ", activity));
                }
                case 5 -> {
                    System.out.print("Enter hashtag: ");
                    String hashtag = scanner.nextLine();
                    newFilter.add(new HashtagCharacterFilterStrategy(hashtag));
                    activeFilters.put("Hashtag", String.join(", ", hashtag));
                }
            }
        }
    }

    private void search() {
        if (searchHistory.hasHistory()) {
            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Search History ▀▄▀▄▀▄▀▄▀▄▀▄");
            List<String> history = searchHistory.getHistory();
            for (int i = 0; i < history.size(); i++) {
                System.out.println(i + 1 + ". " + history.get(i));
            }
        }

        System.out.print("\nChoose from search history (1-5) or enter new search: ");
        String input = scanner.nextLine();

        String searchQuery;
        try {
            int historyChoice = Integer.parseInt(input);
            if (historyChoice > 0 && historyChoice <= searchHistory.getHistory().size()) {
                searchQuery = searchHistory.getHistory().get(historyChoice - 1);
            } else {
                searchQuery = input;
            }
        } catch (NumberFormatException e) {
            searchQuery = input;
        }

        searchHistory.addSearch(searchQuery);
        performSearch(searchQuery);
    }

    private void performSearch(String query) {
        String lowerQuery = query.toLowerCase();

        List<Media> allMedia = facade.browseMedia(items -> items);
        List<Media> foundMedia = allMedia.stream()
                .filter(m -> m.getTitle().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());

        List<Character> allCharacters = facade.browseCharacters(items -> items);
        List<Character> foundCharacters = allCharacters.stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Search Results ▀▄▀▄▀▄▀▄▀▄▀▄");
        boolean found = false;

        if (!foundMedia.isEmpty()) {
            found = true;
            for (int i = 0; i < foundMedia.size(); i++) {
                System.out.println("[Media " + i + 1 + "]" + foundMedia.get(i).getTitle() + " | " + foundMedia.get(i).getType());
            }
        }

        if (!foundCharacters.isEmpty()) {
            found = true;
            for (int i = 0; i < foundCharacters.size(); i++) {
                System.out.println("[Character  " + i + 1 + "]" + foundCharacters.get(i).getName());
            }
        }

        if (!found) {
            System.out.println("No exact matches found.");
            return;
        }

        System.out.println("\n1. View media details");
        System.out.println("2. View character details");
        System.out.println("3. Go back");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1 && !foundMedia.isEmpty()) {
            System.out.print("Choose media number: ");
            int mediaChoice = scanner.nextInt();
            scanner.nextLine();
            if (mediaChoice > 0 && mediaChoice <= foundMedia.size()) {
                Media selectedMedia = facade.viewMediaDetails(foundMedia.get(mediaChoice - 1).getId());
                showMediaDetails(selectedMedia);
            }
        } else if (choice == 2 && !foundCharacters.isEmpty()) {
            System.out.print("Choose character number: ");
            int charChoice = scanner.nextInt();
            scanner.nextLine();
            if (charChoice > 0 && charChoice <= foundCharacters.size()) {
                showCharacterDetails(foundCharacters.get(charChoice - 1));
            }
        }
    }

    private void manageContent() {
        System.out.println("\n1. Delete media");
        System.out.println("2. Delete character");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            List<Media> mediaList = facade.browseMedia(items -> items);
            if (mediaList.isEmpty()) return;

            for (int i = 0; i < mediaList.size(); i++) {
                System.out.println(i + 1 + ". " + mediaList.get(i).getTitle());
            }
            System.out.print("Choose media to delete: ");
            int del = scanner.nextInt();
            scanner.nextLine();

            if (del > 0 && del <= mediaList.size()) {
                facade.deleteMedia(mediaList.get(del - 1).getId());
                System.out.println("Media deleted successfully!");
            }
        } else if (choice == 2) {
            List<Character> characters = facade.browseCharacters(items -> items);
            if (characters.isEmpty()) return;

            for (int i = 0; i < characters.size(); i++) {
                System.out.println(i + 1 + ". " + characters.get(i).getName());
            }
            System.out.print("Choose character to delete: ");
            int del = scanner.nextInt();
            scanner.nextLine();

            if (del > 0 && del <= characters.size()) {
                facade.deleteCharacter(characters.get(del - 1).getId());
                System.out.println("Character deleted successfully!");
            }
        }
    }

    private void manageUsers() {
        List<User> allUsers = facade.getAllUsers();

        if (allUsers.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        for (int i = 0; i < allUsers.size(); i++) {
            User user = allUsers.get(i);
            String role = user.isAdmin() ? "[ADMIN]" : "[USER]";
            System.out.printf("%d. %s %s - %s\n",
                    i + 1,
                    role,
                    user.getUsername(),
                    user.getEmail() != null ? user.getEmail() : "No email"
            );
        }

        System.out.println("\n1. Delete user");
        System.out.println("2. View user profile");
        System.out.println("3. Go back");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> deleteUser(allUsers);
            case 2 -> viewUserProfile(allUsers);
            case 3 -> { return; }
            default -> {
                System.out.println("Invalid option!");
                manageUsers();
            }
        }
        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ All Users ▀▄▀▄▀▄▀▄▀▄▀▄");
    }

    private void deleteUser(List<User> allUsers) {
        System.out.print("\nEnter user number to delete (0 to cancel): ");
        int userChoice = scanner.nextInt();
        scanner.nextLine();

        if (userChoice == 0) {
            manageUsers();
            return;
        }

        if (userChoice < 1 || userChoice > allUsers.size()) {
            System.out.println("Invalid user number!");
            manageUsers();
            return;
        }

        User selectedUser = allUsers.get(userChoice - 1);

        if (selectedUser.getId() == currentUser.getId()) {
            System.out.println("You cannot delete yourself!");
            manageUsers();
            return;
        }

        if (selectedUser.isAdmin()) {
            System.out.println("Cannot delete admin users!");
            manageUsers();
            return;
        }

        System.out.print("Are you sure you want to delete user " + selectedUser.getUsername() + "(yes/no): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("yes")) {
            facade.deleteUser(selectedUser.getId());
            System.out.println("User '" + selectedUser.getUsername() + "' deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }

        manageUsers();
    }

    private void viewUserProfile(List<User> allUsers) {
        System.out.print("\nEnter user number to view profile (0 to cancel): ");
        int userChoice = scanner.nextInt();
        scanner.nextLine();

        if (userChoice == 0) {
            manageUsers();
            return;
        }

        if (userChoice < 1 || userChoice > allUsers.size()) {
            System.out.println("Invalid user number!");
            manageUsers();
            return;
        }

        User selectedUser = allUsers.get(userChoice - 1);
        viewAuthorProfile(selectedUser.getId(), selectedUser.getUsername());
        manageUsers();
    }

    private void viewAuthorProfile(int authorId, String authorName) {
        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ " + authorName + " ▀▄▀▄▀▄▀▄▀▄▀▄");

        List<Media> authorMedia = facade.getUserMedia(authorId);
        System.out.println("\nMedia created: " + authorMedia.size());

        List<Character> authorCharacters = facade.getUserCharacters(authorId);
        System.out.println("Characters created: " + authorCharacters.size());

        try {
            boolean isAuthorAdmin = facade.isUserAdmin(authorId);

            if (!isAuthorAdmin) {
                int favMediaCount = facade.countUserFavoriteMedia(authorId);
                int favCharCount = facade.countUserFavoriteCharacters(authorId);
                System.out.println("Favorites: " + (favMediaCount + favCharCount) + " (" + favMediaCount + " media, " + favCharCount + " characters)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n1. View author's media");
        System.out.println("2. View author's characters");
        System.out.println("3. View author's favorites");
        System.out.println("4. Go back");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> viewAuthorMedia(authorId, authorName, authorMedia);
            case 2 -> viewAuthorCharacters(authorId, authorName, authorCharacters);
            case 3 -> viewAuthorFavorites(authorId, authorName);
        }
    }

    private void viewAuthorMedia(int authorId, String authorName, List<Media> mediaList) {
        if (mediaList.isEmpty()) {
            System.out.println("No media created by this author.");
            System.out.print("Press Enter to go back...");
            scanner.nextLine();
            viewAuthorProfile(authorId, authorName);
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ " + authorName + "'s Media ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < mediaList.size(); i++) {
            System.out.println(i + 1 + ". " + mediaList.get(i).getTitle() + " | " + mediaList.get(i).getType());
        }

        System.out.print("\nChoose media for details (0 to go back): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= mediaList.size()) {
            Media selected = facade.viewMediaDetails(mediaList.get(choice - 1).getId());

            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Media Details ▀▄▀▄▀▄▀▄▀▄▀▄");
            System.out.println(selected.getDetails());
            System.out.println("Author: " + authorName);

            System.out.println("\n1. Add to favorites");
            System.out.println("2. Show all characters");
            System.out.println("3. Go back to author's profile");
            System.out.print("Choose option: ");
            int detailChoice = scanner.nextInt();
            scanner.nextLine();

            switch (detailChoice) {
                case 1 -> {
                    facade.addToFavorites(currentUser.getId(), selected.getId(), true);
                    viewAuthorMedia(authorId, authorName, mediaList);
                }
                case 2 -> {
                    showMediaCharacters(selected);
                    viewAuthorMedia(authorId, authorName, mediaList);
                }
                case 3 -> viewAuthorProfile(authorId, authorName);
            }
        } else {
            viewAuthorProfile(authorId, authorName);
        }
    }

    private void viewAuthorCharacters(int authorId, String authorName, List<Character> characterList) {
        if (characterList.isEmpty()) {
            System.out.println("No characters created by this author.");
            System.out.print("Press Enter to go back...");
            scanner.nextLine();
            viewAuthorProfile(authorId, authorName);
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ " + authorName + "'s Characters ▀▄▀▄▀▄▀▄▀▄▀▄");
        for (int i = 0; i < characterList.size(); i++) {
            System.out.println(i + 1 + ". " + characterList.get(i).getName());
        }

        System.out.print("\nChoose character for details (0 to go back): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= characterList.size()) {
            Character selected = characterList.get(choice - 1);

            System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Character Details ▀▄▀▄▀▄▀▄▀▄▀▄");
            System.out.println(selected.getDetails());

            Media media = facade.viewMediaDetails(selected.getMediaId());
            if (media != null) {
                System.out.println("From Media: " + media.getTitle());
            }
            System.out.println("Author: " + authorName);

            System.out.println("\n1. Add to favorites");
            System.out.println("2. Go back to author's profile");
            System.out.print("Choose option: ");
            int detailChoice = scanner.nextInt();
            scanner.nextLine();

            if (detailChoice == 1) {
                facade.addToFavorites(currentUser.getId(), selected.getId(), false);
            }

            viewAuthorProfile(authorId, authorName);
        } else {
            viewAuthorProfile(authorId, authorName);
        }
    }

    private void viewAuthorFavorites(int authorId, String authorName) {
        List<Media> favoriteMedia = facade.getFavoriteMedia(authorId);
        List<Character> favoriteCharacters = facade.getFavoriteCharacters(authorId);

        if (favoriteMedia.isEmpty() && favoriteCharacters.isEmpty()) {
            System.out.println("No favorites.");
            System.out.print("Press Enter to go back...");
            scanner.nextLine();
            viewAuthorProfile(authorId, authorName);
            return;
        }

        System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ " + authorName + "'s Favorites ▀▄▀▄▀▄▀▄▀▄▀▄");

        if (!favoriteMedia.isEmpty()) {
            System.out.println("\nFavorite Media:");
            for (int i = 0; i < favoriteMedia.size(); i++) {
                System.out.println("M" + i + 1 + ". " + favoriteMedia.get(i).getTitle() + " | " + favoriteMedia.get(i).getType());
            }
        }

        if (!favoriteCharacters.isEmpty()) {
            System.out.println("\nFavorite Characters:");
            for (int i = 0; i < favoriteCharacters.size(); i++) {
                System.out.println("C" + i + 1 + ". " + favoriteCharacters.get(i).getName());
            }
        }

        System.out.print("\nChoose item for details (M1, C2, etc.) or 0 to go back: ");
        String choiceStr = scanner.nextLine();

        if (choiceStr.equals("0")) {
            viewAuthorProfile(authorId, authorName);
            return;
        }

        if (choiceStr.toUpperCase().startsWith("M")) {
            try {
                int index = Integer.parseInt(choiceStr.substring(1)) - 1;
                if (index >= 0 && index < favoriteMedia.size()) {
                    Media selected = facade.viewMediaDetails(favoriteMedia.get(index).getId());

                    System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Media Details ▀▄▀▄▀▄▀▄▀▄▀▄");
                    System.out.println(selected.getDetails());

                    String mediaAuthorName = facade.getMediaAuthorName(selected.getId());
                    System.out.println("Author: " + mediaAuthorName);

                    System.out.println("\n1. Add to favorites");
                    System.out.println("2. Show all characters");
                    System.out.println("3. Go back to author's profile");
                    System.out.print("Choose option: ");
                    int detailChoice = scanner.nextInt();
                    scanner.nextLine();

                    switch (detailChoice) {
                        case 1 -> {
                            facade.addToFavorites(currentUser.getId(), selected.getId(), true);
                            viewAuthorFavorites(authorId, authorName);
                        }
                        case 2 -> {
                            showMediaCharacters(selected);
                            viewAuthorFavorites(authorId, authorName);
                        }
                        case 3 -> viewAuthorProfile(authorId, authorName);
                    }
                }
            } catch (NumberFormatException e) {
                viewAuthorFavorites(authorId, authorName);
            }
        } else if (choiceStr.toUpperCase().startsWith("C")) {
            try {
                int index = Integer.parseInt(choiceStr.substring(1)) - 1;
                if (index >= 0 && index < favoriteCharacters.size()) {
                    Character selected = favoriteCharacters.get(index);

                    System.out.println("\n▀▄▀▄▀▄▀▄▀▄▀▄ Character Details ▀▄▀▄▀▄▀▄▀▄▀▄");
                    System.out.println(selected.getDetails());

                    Media media = facade.viewMediaDetails(selected.getMediaId());
                    if (media != null) {
                        System.out.println("From Media: " + media.getTitle());
                    }

                    String charAuthorName = facade.getMediaAuthorName(selected.getMediaId());
                    System.out.println("Author: " + charAuthorName);

                    System.out.println("\n1. Add to favorites");
                    System.out.println("2. Go back to author's profile");
                    System.out.print("Choose option: ");
                    int detailChoice = scanner.nextInt();
                    scanner.nextLine();

                    if (detailChoice == 1) {
                        facade.addToFavorites(currentUser.getId(), selected.getId(), false);
                    }

                    viewAuthorProfile(authorId, authorName);
                }
            } catch (NumberFormatException e) {
                viewAuthorFavorites(authorId, authorName);
            }
        }
    }

    public static void main(String[] args) {
        ZazManagementSystem system = new ZazManagementSystem();
        system.start();
    }
}
