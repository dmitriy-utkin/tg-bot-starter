package io.github.dmitriyutkin.tgbotstarter.aop.impl;

import io.github.dmitriyutkin.tgbotstarter.aop.AddAdditionalButtonAspect;
import io.github.dmitriyutkin.tgbotstarter.config.OperationRegistry;
import io.github.dmitriyutkin.tgbotstarter.operation.ButtonProvider;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class AddAdditionalButtonAspectImpl {

    private final OperationRegistry operationRegistry;

    @Around("@annotation(addAdditionalButtonAspect)")
    public Object addAdditionalButtons(ProceedingJoinPoint joinPoint, AddAdditionalButtonAspect addAdditionalButtonAspect) throws Throwable {
        Object target = joinPoint.getTarget();
        if (!(target instanceof ButtonProvider)) {
            throw new IllegalStateException(
                    String.format("Method %s cannot be annotated @AddEndButton, class should implements ButtonProvider interface", joinPoint.getSignature().getName()));
        }
        ButtonProvider additionalButtonsProvider = operationRegistry.getButtonProvider(addAdditionalButtonAspect.buttonProviderName());
        if (Objects.isNull(additionalButtonsProvider)) {
            throw new IllegalStateException(String.format("Button provider name is incorrect: %s (nothing found by this name)", addAdditionalButtonAspect.buttonProviderName()));
        }
        List<List<InlineKeyboardButton>> buttons = (List<List<InlineKeyboardButton>>) joinPoint.proceed();
        List<List<InlineKeyboardButton>> additionalButtons = additionalButtonsProvider.getButtons();
        buttons.addAll(additionalButtons);
        return buttons;
    }
}
