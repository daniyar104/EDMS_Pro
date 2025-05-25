package com.example.docplatform.logging;

import com.example.docplatform.model.Document;
import com.example.docplatform.model.User;
import com.example.docplatform.repository.UserRepository;
import com.example.docplatform.service.DocumentActionLogService;
import com.example.docplatform.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class DocumentActionAspect {

    private final DocumentService documentService;
    private final DocumentActionLogService logService;
    private final UserRepository userRepository;

    @After("@annotation(com.example.docplatform.logging.DocumentAction)")
    public void logDocumentAction(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DocumentAction action = method.getAnnotation(DocumentAction.class);

        Object[] args = joinPoint.getArgs();

        String documentNumber = null;
        String userEmail = null;

        for (Object arg : args) {
            if (arg instanceof String str) {
                if (str.contains("@")) userEmail = str;
                else documentNumber = str;
            }
        }

        if (documentNumber == null || userEmail == null) {
            System.err.println("❗ Не удалось определить documentNumber или userEmail для логирования");
            return;
        }

        Document document = documentService.getDocumentByNumber(documentNumber);
        User user = userRepository.findUserByEmail(userEmail).orElse(null);

        if (document != null && user != null) {
            logService.log(document, user, action.type(), action.description());
        }
    }
}
