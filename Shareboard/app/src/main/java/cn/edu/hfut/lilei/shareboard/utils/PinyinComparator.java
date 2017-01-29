package cn.edu.hfut.lilei.shareboard.utils;

import java.util.Comparator;

import cn.edu.hfut.lilei.shareboard.data.GroupMemberInfo;

/**
 * 
 *
 */
public class PinyinComparator implements Comparator<GroupMemberInfo> {

	public int compare(GroupMemberInfo o1, GroupMemberInfo o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
