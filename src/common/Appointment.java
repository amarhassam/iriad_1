package common;

import java.io.Serializable;

public record Appointment(String personId, String date, String time, String center)
        implements Serializable {
    private static final long serialVersionUID = 1L;
}
