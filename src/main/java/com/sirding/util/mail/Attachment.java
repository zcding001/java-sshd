package com.sirding.util.mail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
/**
 * @Described	: 邮件附件
 * @project		: com.sirding.util.mail.Attachment
 * @author 		: zc.ding
 * @date 		: 2017年3月21日
 */
public class Attachment {
	private Map<String, byte[]> mapAttachMent = null;

	public Attachment() {
		mapAttachMent = new HashMap<String, byte[]>();
	}

	public Map<String, byte[]> getMapAttachMent() {
		return mapAttachMent;
	}

	public void setMapAttachMent(Map<String, byte[]> mapAttachMent) {
		this.mapAttachMent = mapAttachMent;
	}

	public void addAttachment(File file) throws IOException {
		mapAttachMent.put(file.getName(), FileUtils.readFileToByteArray(file));
	}

	public void addAttachment(String fileName, byte[] bufFile) throws IOException {
		mapAttachMent.put(fileName, bufFile);
	}
}
