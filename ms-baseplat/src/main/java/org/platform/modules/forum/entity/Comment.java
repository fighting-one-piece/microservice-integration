package org.platform.modules.forum.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.platform.modules.abstr.entity.PKAutoEntity;

/** 帖子评论 */
@Entity
@Table(name = "T_COMMENT")
public class Comment extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;

	/** 频道ID */
	@Column(name = "CHANNEL_ID")
	private Long channelId = null;
	/** 帖子ID */
	@Column(name = "POST_ID")
	private Long postId = null;
	/** 描述 */
	@Column(name = "CONTENT", length = 1000, nullable = false)
	private String content = null;
	/** 图片 */
	@Column(name = "IMAGES", length = 100)
	private String images = null;
	/** 附件 */
	@Column(name = "ATTACHMENTS", length = 100)
	private String attachments = null;
	/** 父级ID */
	@Column(name = "PARENT_ID")
	private Long parentId = null;
	/** 评论数 */
	@Column(name = "COMMENT_COUNT")
	private Integer commentCount = null;
	/** 举报数 */
	@Column(name = "REPORT_COUNT")
	private Integer reportCount = null;
	/** 纬度 */
	@Column(name = "LATITUDE")
	private Double latitude = null;
	/** 经度 */
	@Column(name = "LONGITUDE")
	private Double longitude = null;
	/** 客户设备 */
	@Column(name = "CLIENT_DEVICE", length = 100)
	private String clientDevice = null;
	/** 删除原因 */
	@Column(name = "DELETE_REASON", length = 200)
	private String deleteReason = null;
	/** 删除标志 */
	@Column(name = "DELETE_FLAG", nullable = false)
	private Boolean deleteFlag = false;

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public String getAttachments() {
		return attachments;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Integer getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

	public Integer getReportCount() {
		return reportCount;
	}

	public void setReportCount(Integer reportCount) {
		this.reportCount = reportCount;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getClientDevice() {
		return clientDevice;
	}

	public void setClientDevice(String clientDevice) {
		this.clientDevice = clientDevice;
	}

	public String getDeleteReason() {
		return deleteReason;
	}

	public void setDeleteReason(String deleteReason) {
		this.deleteReason = deleteReason;
	}

	public Boolean getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

}
