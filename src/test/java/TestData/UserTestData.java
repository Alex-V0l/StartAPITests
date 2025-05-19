package TestData;

import Models.User;

public class UserTestData {
    public static final User DEFAULT_USER = new User
            (98710631690823L, "test_user_neo_2025", "Tomas", "Anderson", "neo2025@example.com",
                    "followthewhiterabbit", "+5781438", 0);

    public static final User INVALID_USER = new User();
}

