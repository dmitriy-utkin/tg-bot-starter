package io.github.dmitriyutkin.tgbotstarter.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MarkupType {
    MARKDOWN("Markdown"),
    MARKDOWNV2("MarkdownV2"),
    HTML("html"),
    NONE(null);

    private final String parseModeName;
}
