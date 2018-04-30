/*
 *
 *  * Copyright 2002-2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.ifreelight.dyna.security;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Vector;

public class SessionStoreToken {

    private int tokenSize;


    @Autowired
    private HttpServletRequest request;


    public void setTokenSize(int tokenSize) {
        this.tokenSize = tokenSize;
    }

    private Vector<String> getSessionTokens() {
        Vector<String> tokens = (Vector<String>) request.getSession().getAttribute("_session_token_vector");
        if (tokens == null) {
            tokens = new Vector<String>(tokenSize);
            request.getSession().setAttribute("_session_token_vector", tokens);
        }
        return tokens;
    }

    public boolean contains(String token) {
        return getSessionTokens().contains(token);
    }

    public void remove(String token) {
        getSessionTokens().remove(token);
    }

    public synchronized void addToken(String token) {
        Vector<String> stringVector = getSessionTokens();
        if (stringVector.size() < tokenSize) {
            stringVector.add(token);
        } else {
            stringVector.remove(stringVector.firstElement());
            stringVector.add(token);
        }

    }

}
