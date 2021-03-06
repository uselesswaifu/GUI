package moe.him188.gui.utils;

import org.jetbrains.annotations.NotNull;

/**
 * @author Him188moe @ GUI Project
 */
public class InputTypeDouble extends InputType<Double> {
    @NotNull
    @Override
    public Double parseResponse(String content) throws InputFormatException {
        try {
            return Double.parseDouble(content);
        } catch (NumberFormatException e) {
            throw new InputFormatException(InputFormatException.ReasonDefaults.NUMBER_FORMAT, content, e);
        }
    }
}
