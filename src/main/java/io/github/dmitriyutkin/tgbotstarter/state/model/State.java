package io.github.dmitriyutkin.tgbotstarter.state.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
public class State {

    private String chatId;

    @Builder.Default
    private Map<Integer, String> stateStageInfo = new HashMap<>();

    @Builder.Default
    private Map<String, Object> states = new HashMap<>();
}
