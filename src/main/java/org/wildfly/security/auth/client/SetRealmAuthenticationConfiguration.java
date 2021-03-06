/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.security.auth.client;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;

/**
 * @author <a href="mailto:kkhan@redhat.com">Kabir Khan</a>
 */
class SetRealmAuthenticationConfiguration extends AuthenticationConfiguration {

    private final String realm;

    SetRealmAuthenticationConfiguration(final AuthenticationConfiguration parent, final String realm) {
        super(parent.without(SetCallbackHandlerAuthenticationConfiguration.class));
        this.realm = realm;
    }

    void handleCallback(final Callback[] callbacks, final int index) throws IOException, UnsupportedCallbackException {
        Callback callback = callbacks[index];
        if (callback instanceof RealmCallback) {
            RealmCallback realmCallback = (RealmCallback) callback;
            realmCallback.setText(realm != null ? realm : realmCallback.getDefaultText());
            return;
        } else if (callback instanceof RealmChoiceCallback) {
            RealmChoiceCallback realmChoiceCallback = (RealmChoiceCallback) callback;
            if (realm == null) {
                realmChoiceCallback.setSelectedIndex(realmChoiceCallback.getDefaultChoice());
            } else {
                String[] choices = realmChoiceCallback.getChoices();
                for (int i = 0; i < choices.length; i++) {
                    if (realm.equals(choices[i])) {
                        realmChoiceCallback.setSelectedIndex(i);
                        break;
                    }
                }
            }
            return;
        }
        super.handleCallback(callbacks, index);
    }

    AuthenticationConfiguration reparent(final AuthenticationConfiguration newParent) {
        return new SetRealmAuthenticationConfiguration(newParent, realm);
    }

    @Override
    StringBuilder asString(StringBuilder sb) {
        return parentAsString(sb).append("realm=").append(realm).append(',');
    }

}
