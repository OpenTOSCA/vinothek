package org.opentosca.ui.admin.action;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.opentosca.ui.admin.action.client.ContainerClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * This Action recieves a File of the Client Browser which should be a Cloud
 * Service Archive. The it takes it and uploads it to OpenTOSCA.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class UploadCSARAction extends ActionSupport {

    private static final long serialVersionUID = -5536842710743698964L;
    private File file;
    private String fileContentType;
    private String fileFileName;

    @Override
    public String execute() {

	System.out.println("begin uploading the CSAR " + fileFileName);

	if (null == file) {
	    // there is no file to send
	    return "success";
	}

	// some copy magic. it has to be done, so the file has the correct name.
	String folderPath = file.getParentFile().getAbsolutePath();
	String filePath = null;

	File fileToCreate = new File(folderPath, fileFileName);
	try {
	    FileUtils.copyFile(file, fileToCreate);
	} catch (IOException e) {
	    System.out.println("There was an error during copying the CSAR with the message: " + e.getLocalizedMessage());
	    fileToCreate = file;
	}

	filePath = fileToCreate.getAbsolutePath();

	// upload
	ContainerClient client = ContainerClient.getInstance();
	List<String> result = client.uploadCSAR(filePath);

	for (String str : result) {
	    System.out.println(str);
	}

	System.out.println("finished uploading the CSAR " + fileFileName);

	if (result.get(0).equalsIgnoreCase("Created")) {
	    return "success";
	} else {
	    return result.get(0);
	}
    }

    public File getFile() {

	return file;
    }

    public String getFileContentType() {

	return fileContentType;
    }

    public String getFileFileName() {

	return fileFileName;
    }

    public void setFile(File file) {

	this.file = file;
    }

    public void setFileContentType(String fileContentType) {

	this.fileContentType = fileContentType;
    }

    public void setFileFileName(String fileFileName) {

	this.fileFileName = fileFileName;
    }

}
