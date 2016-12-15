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
package de.bund.bva.isyfact.common.web.exception.web;

import java.net.SocketException;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.bund.bva.isyfact.common.web.GuiController;
import de.bund.bva.isyfact.common.web.common.konstanten.EreignisSchluessel;
import de.bund.bva.isyfact.common.web.exception.common.AusnahmeIdMapper;
import de.bund.bva.isyfact.common.web.exception.common.FehlerInformation;
import de.bund.bva.isyfact.common.web.exception.common.FehlerInformation.Fehlertyp;
import de.bund.bva.isyfact.common.web.exception.common.FehlertextProviderImpl;
import de.bund.bva.isyfact.common.web.exception.common.FehlertextUtil;
import de.bund.bva.isyfact.common.web.konstanten.FehlerSchluessel;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;

/**
 * Der ErrorController wird zum Darstellen der allgemeinen GUI-Fehlerseite verwendet. Zusätzlich dazu
 * behandelt dieser technische Fehler, welche innerhalb der Flows auftreten.
 *
 * @author Capgemini, Andreas Hörning
 */
public class ErrorController implements ApplicationContextAware, GuiController {

    /** Der Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(ErrorController.class);

    /**
     * Der Request-Parameter für die unique ID.
     */
    private static final String REQUEST_PARAMETER_UNIQUE_ID = "uniqueId";

    /**
     * Der Request-Parameter für die Fehler-ID.
     */
    private static final String REQUEST_PARAMETER_FEHLER_ID = "fehlerId";

    /**
     * Der Application-Kontext.
     */
    private ApplicationContext applicationContext;

    /**
     * Initialisiert das Model.
     *
     * @param errorModel
     *            Das Model.
     * @param t
     *            Der übergebene Fehler.
     */
    public void initialisiereModel(ErrorModel errorModel, Throwable t) {

        Throwable socketException = getClientAbortException(t);

        // Erzeuge Fehler
        Map<String, String> parameters =
            FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        FehlerInformation fehlerInformation = null;

        if (parameters.get(REQUEST_PARAMETER_UNIQUE_ID) != null
            && parameters.get(REQUEST_PARAMETER_FEHLER_ID) != null) {

            // Es wurde ein Fehler übermittelt. Erzeuge Fehlerinformation
            fehlerInformation = new FehlerInformation();
            fehlerInformation.setFehlerId(parameters.get(REQUEST_PARAMETER_FEHLER_ID));
            fehlerInformation.setUuid(parameters.get(REQUEST_PARAMETER_UNIQUE_ID));
            // Request-übermittelte Fehler sind immer technisch
            fehlerInformation.setTyp(Fehlertyp.TECHNISCH);
            // Ein Fehlertext wird nicht benötigt, da die Ausnahme schon geloggt wurde
            fehlerInformation.setFehlernachricht(null);

            errorModel.setFehlerText(fehlerInformation.getGuiErrorMessage());

        } else {
            // Unterscheidung eingebaut, da das BVA keine Error-Log-Einträge haben möchte, die auftreten, wenn
            // der Browser des Nutzers Verbindungen schließt (mehrfacher Klick auf einen Download, usw.).
            if (socketException != null) {
                LOG.error(EreignisSchluessel.E_CLIENT_VERBINDUNG, "Client-Verbindungsfehler", t);
                fehlerInformation = FehlertextUtil.ermittleFehlerinformation(t,
                    this.applicationContext.getBean(AusnahmeIdMapper.class));
            } else {
                // Erzeuge neuen Fehler
                fehlerInformation = FehlertextUtil.schreibeLogEintragUndErmittleFehlerinformation(t,
                    this.applicationContext.getBean(AusnahmeIdMapper.class), LOG);
            }
        }

        // Ermittle den GUI-Fehlertext
        errorModel.setFehlerText(fehlerInformation.getGuiErrorMessage());
        errorModel.setFehlerUeberschrift(fehlerInformation.getGuiErrorMessageTitle());
    }

    /**
     * Gibt eine Client-Verbindungsfehler zurück, falls dieser in der übergebenen Exception existiert.
     *
     * @param t
     *            Die Exception/Throwable.
     * @return Der Client-Verbindungsfehler.
     */
    private Throwable getClientAbortException(Throwable t) {

        if (t == null) {
            return null;
        }

        if (t instanceof SocketException) {
            return t;
        }

        // Nicht gefunden, überprüfe Cause.
        return getClientAbortException(t.getCause());

    }

    /**
     * Gibt den Fehlertext für Ajax-Fehlermeldungen zurück.
     * @return Der Fehlertext.
     */
    public String getAjaxErrorMessage() {
        return new FehlertextProviderImpl().getMessage(FehlerSchluessel.FEHLERTEXT_GUI_AJAX);
    }

    /**
     * Gibt den Titel des Fehlertexts für Ajax-Fehlermeldungen zurück.
     * @return Der Titel.
     */
    public String getAjaxErrorMessageTitle() {
        return new FehlertextProviderImpl()
            .getMessage(FehlerSchluessel.FEHLERTEXT_GUI_TECHNISCH_UEBERSCHRIFT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
