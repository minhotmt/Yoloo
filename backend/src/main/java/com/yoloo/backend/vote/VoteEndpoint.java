package com.yoloo.backend.vote;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.users.User;

import com.yoloo.backend.Constants;
import com.yoloo.backend.authentication.authenticators.FacebookAuthenticator;
import com.yoloo.backend.authentication.authenticators.GoogleAuthenticator;
import com.yoloo.backend.authentication.authenticators.YolooAuthenticator;
import com.yoloo.backend.comment.CommentShardService;
import com.yoloo.backend.question.QuestionShardService;
import com.yoloo.backend.validator.Validator;
import com.yoloo.backend.validator.rule.common.AuthenticationRule;
import com.yoloo.backend.validator.rule.common.IdValidationRule;
import com.yoloo.backend.validator.rule.common.NotFoundRule;

import java.util.logging.Logger;

import javax.inject.Named;

@Api(
        name = "yolooApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = Constants.API_OWNER,
                ownerName = Constants.API_OWNER,
                packagePath = Constants.API_PACKAGE_PATH
        )
)
@ApiClass(
        resource = "votes",
        clientIds = {
                Constants.ANDROID_CLIENT_ID,
                Constants.IOS_CLIENT_ID,
                Constants.WEB_CLIENT_ID},
        audiences = {Constants.AUDIENCE_ID},
        authenticators = {
                GoogleAuthenticator.class,
                FacebookAuthenticator.class,
                YolooAuthenticator.class
        }
)
final class VoteEndpoint {

    private static final Logger logger =
            Logger.getLogger(VoteEndpoint.class.getSimpleName());

    @ApiMethod(
            name = "questions.vote",
            path = "questions/{votableId}/votes",
            httpMethod = ApiMethod.HttpMethod.POST)
    public void vote(@Named("votableId") String websafeVotableId,
                     @Named("dir") Vote.Direction direction,
                     final User user) throws ServiceException {

        Validator.builder()
                .addRule(new IdValidationRule(websafeVotableId))
                .addRule(new AuthenticationRule(user))
                .addRule(new NotFoundRule(websafeVotableId))
                .validate();

        VoteController
                .newInstance(
                        QuestionShardService.newInstance(),
                        CommentShardService.newInstance())
                .vote(websafeVotableId, direction, user);
    }
}
