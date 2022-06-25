package util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {

    public void write(final JsonWriter jsonWriter, final LocalDateTime date) throws IOException {
        if (date != null)
            jsonWriter.value(date.toString());
        else
            jsonWriter.nullValue();
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString());
    }
}
