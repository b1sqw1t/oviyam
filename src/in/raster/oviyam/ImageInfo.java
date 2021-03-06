/* ***** BEGIN LICENSE BLOCK *****
* Version: MPL 1.1/GPL 2.0/LGPL 2.1
*
* The contents of this file are subject to the Mozilla Public License Version
* 1.1 (the "License"); you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
*
* Software distributed under the License is distributed on an "AS IS" basis,
* WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
* for the specific language governing rights and limitations under the
* License.
*
* The Original Code is part of Oviyam, an web viewer for DICOM(TM) images
* hosted at http://skshospital.net/pacs/webviewer/oviyam_0.6-src.zip
*
* The Initial Developer of the Original Code is
* Raster Images
* Portions created by the Initial Developer are Copyright (C) 2007
* the Initial Developer. All Rights Reserved.
*
* Contributor(s):
* Babu Hussain A
* Meer Asgar Hussain B
* Prakash J
* Suresh V
*
* Alternatively, the contents of this file may be used under the terms of
* either the GNU General Public License Version 2 or later (the "GPL"), or
* the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
* in which case the provisions of the GPL or the LGPL are applicable instead
* of those above. If you wish to allow use of your version of this file only
* under the terms of either the GPL or the LGPL, and not to allow others to
* use your version of this file under the terms of the MPL, indicate your
* decision by deleting the provisions above and replace them with the notice
* and other provisions required by the GPL or the LGPL. If you do not delete
* the provisions above, a recipient may use your version of this file under
* the terms of any one of the MPL, the GPL or the LGPL.
*
* ***** END LICENSE BLOCK ***** */

package in.raster.oviyam;

import de.iftm.dcm4che.services.CDimseService;
import de.iftm.dcm4che.services.ConfigProperties;
import de.iftm.dcm4che.services.GenericDicomURL;
import in.raster.oviyam.model.InstanceModel;
import in.raster.oviyam.util.IDataSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import org.apache.log4j.Logger;
import java.text.ParseException;

/**
 *
 * @author asgar
 */
public class ImageInfo {

    //Initialize Logger
    private static Logger log = Logger.getLogger(ImageInfo.class);

    private ArrayList<InstanceModel> instancesList;

    //Constructor
    public ImageInfo() {
        instancesList = new ArrayList<InstanceModel>();
    }

    /**
     * Queries (cFIND) the Instance information from the machine (dcmProtocol://aeTitle!hostName:port).
     *
     * @param patientID
     * @param studyInstanceUID
     * @param seriesInstanceUID
     * @param SOPInstanceUID
     * @param dcmURL
     */
    public void callFindWithQuery(String patientID, String studyInstanceUID, String seriesInstanceUID, String SOPInstanceUID, String dcmURL) {
        ConfigProperties cfgProperties;
        Vector<IDataSet> dsVector;
        CDimseService cDimseService;

        try {
            cfgProperties = new ConfigProperties(ImageInfo.class.getResource("/resources/Image.cfg"));
        } catch(IOException e) {
            log.error("Error while loading configuration properties");
            return;
        }

        //Setting filter values for query
        cfgProperties.put("key.PatientID", patientID);
        cfgProperties.put("key.StudyInstanceUID", studyInstanceUID);
        cfgProperties.put("key.SeriesInstanceUID", seriesInstanceUID);
        if(SOPInstanceUID != null) {
            cfgProperties.put("key.SOPInstanceUID", SOPInstanceUID);
        }

        //Create object for DcmURL
        GenericDicomURL url = new GenericDicomURL(dcmURL);

        //Create object for CDimseService
        try {
            cDimseService = new CDimseService(cfgProperties, url);
        } catch(ParseException pe) {
            log.error("Unable to create CDimseService instance ", pe);
            return;
        }

        // Query result using cFIND
        try {
            dsVector = cDimseService.cFIND();
        } catch(Exception e) {
            log.error("Error while querying ", e);
            return;
        }

        // Get the Dataset from the dsVector and add it to the instances ArrayList<InstanceModel>
        for(int i=0; i<dsVector.size(); i++) {
            try {
                instancesList.add(new InstanceModel(dsVector.elementAt(i)));
            } catch(Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

    }

    /**
     * Getter for instances ArrayList<InstanceModel>,
     * @return the instance of ArrayList<InstanceModel>.
     */
    public ArrayList<InstanceModel> getInstancesList() {
        return instancesList;
    }

}
