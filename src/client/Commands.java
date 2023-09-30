package client;

public enum Commands {
    PRESS_MOUSE(-1),
    RELEASE_MOUSE(-2),
    PRESS_KEY(-3),
    RELEASE_KEY(-4),
    MOVE_MOUSE(-5),
    CLICK_MOUSE(-6),
    MOUSE_WHEEL_MOVED(-7),
    MOUSE_DRAGGED(-8),
    PASTE_TEXT(-9);

    private int abbrev;

    Commands(int abbrev) {
        this.abbrev = abbrev;
    }

    public int getAbbrev() {
        return this.abbrev;
    }
}
