package ru.easyjava.easymock;

import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.easyjava.easymock.entity.User;
import ru.easyjava.easymock.service.ApplicationRegistry;
import ru.easyjava.easymock.service.AuthorizationService;
import ru.easyjava.easymock.service.RequestLimitingService;
import ru.easyjava.easymock.service.TokenService;

import java.util.Arrays;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ParameterizedAuthorizationServiceTest {
    private static final String TOKEN = "TESTTOKEN";
    private static final String APP_ID = "TESTAPP";

    private static final User USER = new User();

    @Rule
    public EasyMockRule em = new EasyMockRule(this);

    @Mock
    private TokenService tokenService;

    @Mock
    private ApplicationRegistry applicationRegistry;

    @Mock
    private RequestLimitingService requestLimitingService;

    @TestSubject
    private AuthorizationService testedObject = new AuthorizationService();

    @Parameterized.Parameters(name = "{index}: Token: {1}, User: {2}, AppID: {3}, appValidity: {4}, noOveruser: {5}, noExtendedOveruse: {6}, Result: {7}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {TOKEN, USER, APP_ID, true, true, null, true}, //Happy path
                {TOKEN, USER, APP_ID, true, false, true, true}, //Extended overuse
                {TOKEN, null, null, null, null, null, false}, //Token not valid
                {TOKEN, USER, null, null, null, null, false}, //No app id
                {TOKEN, USER, APP_ID, false, null, null, false}, //App id is invalid
                {TOKEN, USER, APP_ID, true, false, false, false}, //Resources overused
                {TOKEN, USER, APP_ID, true, null, null, false}, //NPE in limiting service
                {TOKEN, USER, APP_ID, true, false, null, false} //NPE in limiting service
        });
    }

    private String token;
    private User u;
    private String appId;
    private Boolean appValidity;
    private Boolean overuse;
    private Boolean extendedOveruse;
    private Boolean result;

    public ParameterizedAuthorizationServiceTest(String token, User u, String appId, Boolean appValidity, Boolean overuse, Boolean extendedOveruse, Boolean result) {
        this.token = token;
        this.u = u;
        this.appId = appId;
        this.appValidity = appValidity;
        this.overuse = overuse;
        this.extendedOveruse = extendedOveruse;
        this.result = result;
    }

    @Test
    public void testAuthorize() {
        expect(tokenService.validateToken(token)).andStubReturn(u);
        expect(tokenService.getApplicationId(token)).andStubReturn(appId);
        expect(applicationRegistry.validateApplicationId(appId)).andStubReturn(appValidity);
        if (overuse != null) {
            expect(requestLimitingService.checkLimit(u, appId)).andStubReturn(overuse);
        } else {
            expect(requestLimitingService.checkLimit(u, appId)).andThrow(new NullPointerException());
        }
        if (extendedOveruse != null) {
            expect(requestLimitingService.checkExtendedLimit(u)).andStubReturn(extendedOveruse);
        } else {
            expect(requestLimitingService.checkExtendedLimit(u)).andThrow(new NullPointerException());
        }

        replay(tokenService, applicationRegistry, requestLimitingService);

        assertThat(testedObject.authorize(token), is(result));
    }
}