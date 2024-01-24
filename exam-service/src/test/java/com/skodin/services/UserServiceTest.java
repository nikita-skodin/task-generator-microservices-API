package com.skodin.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class UserServiceTest {

    private final UserService userService = new UserService();
    private final String incorrectHeader = "incorrectHeader";
    private final String headerWithTeacherRole = "Bearer eyJhbGciOiJIUzI1NiJ9." +
                                                 "eyJpZCI6OTksInJvbGVzIjpbeyJhdXRob3JpdHkiOiJST0xFX1RFQUNIRVIifV0sInN1YiI6IjFuaWtpdGEiLCJpYXQiOjE3MDYwMDU3NzYsImV4cCI6MTcwNjA5MjE3Nn0." +
                                                 "WpU7Yc2GmOzmmXMHy3HiHaMUo6BaQB-XRxyBBO63Thg";
    private final String headerWithStudentRole = "Bearer eyJhbGciOiJIUzI1NiJ9." +
                                                 "eyJpZCI6OTgsInJvbGVzIjpbeyJhdXRob3JpdHkiOiJST0xFX1NUVURFTlQifV0sInN1YiI6Im5pa2l0YSIsImlhdCI6MTcwNjA0NDE2MiwiZXhwIjoxNzA2MTMwNTYyfQ." +
                                                 "Q-DpHMdPPPhu-laX0ZOljLYdZiOGdkz0SpPW69UECyc";

    @Test
    void getRoleFromAuthHeader_incorrectAuthHeader_returnsFalse() {
        boolean hasSomeRole = userService.getRoleFromAuthHeader(incorrectHeader, "SOME_ROLE");

        assertFalse(hasSomeRole);
    }

    @Test
    void getRoleFromAuthHeader_tokenHasSuitableRole_returnsTrue() {
        boolean hasSomeRole = userService.getRoleFromAuthHeader(headerWithTeacherRole, "ROLE_TEACHER");

        assertTrue(hasSomeRole);
    }

    @Test
    void getRoleFromAuthHeader_tokenHasNotSuitableRole_returnsFalse() {
        boolean hasSomeRole = userService.getRoleFromAuthHeader(headerWithTeacherRole, "ROLE_STUDENT");

        assertFalse(hasSomeRole);
    }

    @Test
    void isTeacher_incorrectAuthHeader_returnsFalse() {
        boolean isTeacher = userService.isTeacher(incorrectHeader);

        assertFalse(isTeacher);
    }

    @Test
    void isTeacher_tokenHasRoleTeacher_returnsTrue() {
        boolean isTeacher = userService.isTeacher(headerWithTeacherRole);

        assertTrue(isTeacher);
    }

    @Test
    void isTeacher_tokenHasNotRoleTeacher_returnsFalse() {
        boolean isTeacher = userService.isTeacher(headerWithStudentRole);

        assertFalse(isTeacher);
    }

    @Test
    void isStudent_incorrectAuthHeader_returnsFalse() {
        boolean isStudent = userService.isStudent(incorrectHeader);

        assertFalse(isStudent);
    }

    @Test
    void isStudent_tokenHasRoleStudent_returnsTrue() {
        boolean isStudent = userService.isStudent(headerWithStudentRole);

        assertTrue(isStudent);
    }

    @Test
    void isStudent_tokenHasNotRoleStudent_returnsFalse() {
        boolean isStudent = userService.isStudent(headerWithTeacherRole);

        assertFalse(isStudent);
    }


}