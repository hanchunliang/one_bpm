package com.sinosoft.one.bpm.identity;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.task.identity.UserGroupCallback;

public class BpmDefaultUserGroupCallback implements UserGroupCallback {

	public boolean existsUser(String userId) {
		// accept all by default
		return true;
	}

	public boolean existsGroup(String groupId) {
		// accept all by default
		return true;
	}

	public List<String> getGroupsForUser(final String userId, List<String> groupIds,
			List<String> allExistingGroupIds) {
		if (groupIds != null) {
			List<String> retList = new ArrayList<String>(groupIds);
			// merge all groups
			if (allExistingGroupIds != null) {
				for (String grp : allExistingGroupIds) {
					if (!retList.contains(grp)) {
						retList.add(grp);
					}
				}
			}
			return retList;
		} else {
			// return empty list by default
			return new ArrayList<String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -4563550033615913593L;

				{
					add("");
				}
			};
		}
	}
}
