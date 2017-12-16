package birkan.nfcsinifyoklama.Objects;

/**
 * Created by birkan on 1.04.2017.
 */

public class Lesson {

    private int lesson_id;
    private String name;

    public Lesson(int lesson_id, String name) {
        this.lesson_id = lesson_id;
        this.name = name;
    }

    public Lesson() {
    }

    public int getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(int lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
