/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.isyfact.common.web.layout;

/**
 * Kapselt Typen von Icons.
 * 
 * @author Capgemini
 * @version $Id: IconTyp.java 123758 2014-10-10 10:01:14Z sdm_ahoerning $
 */
public enum IconTyp {

    /** Info. */
    INFO("info");

    /** Der Suffix der CSS-Icon-Klasse. */
    private String cssSuffix;

    /**
     * Konstruktor.
     * 
     * @param cssSuffix
     *            Der Suffix der CSS-Icon-Klasse.
     */
    private IconTyp(String cssSuffix) {
        this.cssSuffix = cssSuffix;
    }

    public String getCssSuffix() {
        return cssSuffix;
    }

}
