package TestData;

import Models.User;

public class UserTestData {
    public static final User DEFAULT_USER = new User
            (0L, "test_user_neo_2025", "Tomas", "Anders", "neo2025@example.com", "redpillbluepill123", "+79001234567", 0);

    public static final User INVALID_USER = new User();
}

