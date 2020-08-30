package tr.com.ogedik.authentication.validation.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.ogedik.authentication.exception.AuthenticationErrorType;
import tr.com.ogedik.authentication.model.AuthenticationGroup;
import tr.com.ogedik.authentication.service.GroupService;
import tr.com.ogedik.commons.expection.ErrorException;
import tr.com.ogedik.commons.validator.Validator;

/**
 * @author orkun.gedik
 */
@Component
public class GroupCreateValidator<T extends AuthenticationGroup> implements Validator<T> {

    @Autowired
    private GroupService groupService;

    @Override
    public void validate(T validationRequest) {
        if (groupService.isExist(validationRequest.getName())) {
            throw new ErrorException(AuthenticationErrorType.GROUP_EXIST, validationRequest.getName());
        }
    }
}
