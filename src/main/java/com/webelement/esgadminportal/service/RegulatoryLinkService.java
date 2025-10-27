package com.webelement.esgadminportal.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.webelement.esgadminportal.common.BankInfoDTO;
import com.webelement.esgadminportal.common.CommonFunction;
import com.webelement.esgadminportal.entity.GalleryEntity;
import com.webelement.esgadminportal.entity.LinkEntity;
import com.webelement.esgadminportal.entity.TransactionEntity;
import com.webelement.esgadminportal.repo.LinkRepo;

@Service
public class RegulatoryLinkService {

	@Autowired
	private LinkRepo linkRepo;

	@Autowired
	private CommonFunction commonFunction;

	public Page<LinkEntity> findRegulatoryLinkDetails(int page, int size, int statusIndex, String search) {
		return linkRepo.findLinkDetails(PageRequest.of(page, size), statusIndex, search);
	}

	public void addLink(LinkEntity link, HttpServletRequest httpRequest) {
		// ✅ First check if name already exists with status 1 or 2
		int existingCount = linkRepo.countByNameActive(link.getName());
		if (existingCount > 0) {
			throw new RuntimeException("Link with this name already exists.");
		}

		link.setStatus((short) 1); // default active
		link.setRegDate(new Timestamp(System.currentTimeMillis()));
		link.setModDate(new Timestamp(System.currentTimeMillis()));

		// Save the entity
		LinkEntity savedLink = linkRepo.save(link);

		// Now get the generated ID
		Integer uid = savedLink.getLinkId();
		System.out.println("uid : " + uid);

		// Create history AFTER save
		commonFunction.createHistoryAccess(link.getUserId(), commonFunction.resolveClientIp(httpRequest),
				commonFunction.getLocalIp(), "Link Added", 4, uid, -1);
	}

	public void updateLink(LinkEntity link, HttpServletRequest httpRequest) {
		// ✅ Check for duplicates excluding current id
		Optional<LinkEntity> existing = linkRepo.findByNameIgnoreCaseAndLinkIdNotAndStatusIn(link.getName(),
				link.getLinkId(), Arrays.asList((short) 1, (short) 2) // active/pending
		);

		if (existing.isPresent()) {
			throw new RuntimeException("Link with this name already exists.");
		}

		link.setModDate(new Timestamp(System.currentTimeMillis()));

		LinkEntity updatedLink = linkRepo.save(link);

		commonFunction.createHistoryAccess(link.getUserId(), commonFunction.resolveClientIp(httpRequest),
				commonFunction.getLocalIp(), "Link Updated", 4, updatedLink.getLinkId(), -1);
	}

	public LinkEntity getById(Integer id) {
		return linkRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Link not found with ID: " + id));
	}

	public void updateLink(Integer id, LinkEntity link) {
		LinkEntity existing = getById(id);
		existing.setName(link.getName());
		existing.setUrl(link.getUrl());
		existing.setStatus(link.getStatus());
		existing.setUserId(link.getUserId());
		existing.setModDate(new Timestamp(System.currentTimeMillis()));
		linkRepo.save(existing);
	}

	public void deleteLink(Integer id) {
		LinkEntity existing = getById(id);
		existing.setStatus((short) 3); // soft delete
		existing.setModDate(new Timestamp(System.currentTimeMillis()));
		linkRepo.save(existing);
	}

	public boolean deleteLinkById(Integer linkId, Integer userId, HttpServletRequest httpRequest) {
		Optional<LinkEntity> optional = linkRepo.findById(linkId);

		if (!optional.isPresent()) {
			return false; // Record not found
		}

		LinkEntity entity = optional.get();
		entity.setStatus((short) 3);
		entity.setUserId(userId);
		String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Timestamp timestamp = Timestamp.valueOf(dateStr);
		entity.setModDate(timestamp);

		linkRepo.save(entity); // Save updated entity
		commonFunction.createHistoryAccess(userId, commonFunction.resolveClientIp(httpRequest),
				commonFunction.getLocalIp(), "Link Deleted", 4, linkId, -1);
		return true;
	}

	public LinkEntity getRegulatoryLinkById(int linkId) {
		LinkEntity link = linkRepo.getLinkById(linkId);

		if (link != null) {
			// Append transaction history
			List<TransactionEntity> history = getTransactionLogs(4, linkId);
			// System.out.println("history : " + history);
			link.setTransactionhistory(history);
		}

		return link;
	}

	public List<TransactionEntity> getTransactionLogs(int moduleId, Integer recordId) {
		List<Object[]> results = linkRepo.getTransactionLogs(moduleId, recordId);
		System.out.println("results : " + results);
		return results.stream().map(obj -> {
			TransactionEntity dto = new TransactionEntity();
			dto.setEntryDate((String) obj[0]);
			dto.setName((String) obj[1]);
			dto.setAction((String) obj[2]);
			dto.setUserId(obj[3] != null ? ((Number) obj[3]).intValue() : null);
			dto.setFlag((String) obj[4]);
			return dto;
		}).collect(Collectors.toList());
	}

}
