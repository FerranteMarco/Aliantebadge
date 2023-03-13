package com.example.aliantebadge;


public class Variable {
    public static String giuseppe_uid = "WMteJAS3jZW3TsCaLsErHKpCo6u2";
    public static String marco_uid = "bJhlH7o2ZpfZJyx7dA0browV2uo2";
    public static int dayInMilli = 86400000;

    private static boolean versionToUpdate;
    private static boolean lastVersionAvailable;

    private static boolean admin ;

    public static boolean isLastVersionAvailable() {
        return lastVersionAvailable;
    }

    public static void setLastVersionAvailable(boolean lastVersionAvailable) {
        Variable.lastVersionAvailable = lastVersionAvailable;
    }

    public static boolean isAdmin(String currentUserLocalDB) {

        return (currentUserLocalDB.equals(giuseppe_uid) || currentUserLocalDB.equals(marco_uid)) && getAdmin();
    }

    public static boolean isaBoolean(String currentUserLocalDB) {
        return currentUserLocalDB.equals(giuseppe_uid);
    }

    public static boolean isOwner(String currentUserLocalDB) {

        return currentUserLocalDB.equals(marco_uid);
    }

    public static boolean getAdmin() {
        return admin;
    }

    public static void setAdmin(boolean admin) {
        Variable.admin = admin;
    }

    public static boolean isVersionToUpdate() {
        return versionToUpdate;
    }

    public static void setVersionIsToUpdate(boolean version) {
        Variable.versionToUpdate = version;
    }
}
