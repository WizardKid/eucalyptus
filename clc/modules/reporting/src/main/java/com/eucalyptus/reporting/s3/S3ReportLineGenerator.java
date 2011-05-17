package com.eucalyptus.reporting.s3;

import java.util.*;

import com.eucalyptus.reporting.GroupByCriterion;
import com.eucalyptus.reporting.Period;
import com.eucalyptus.reporting.units.Units;

public class S3ReportLineGenerator
{
	private static S3ReportLineGenerator instance;
	
	public static S3ReportLineGenerator getInstance()
	{
		if (instance == null) {
			instance = new S3ReportLineGenerator();
		}
		return instance;
	}
	
	private S3ReportLineGenerator()
	{
		
	}
	
	public List<S3ReportLine> getReportLines(Period period,
			GroupByCriterion criterion,	Units displayUnits)
	{
		return getReportLines(period, null, criterion, displayUnits);
	}

	public List<S3ReportLine> getReportLines(Period period, GroupByCriterion groupByCrit,
			GroupByCriterion crit, Units displayUnits)
	{
		Map<S3ReportLineKey, S3ReportLine> reportLineMap =
			new HashMap<S3ReportLineKey, S3ReportLine>();
		
		S3UsageLog usageLog = S3UsageLog.getS3UsageLog();
		Map<S3SnapshotKey, S3UsageSummary> usageMap = 
			usageLog.getUsageSummaryMap(period);
		for (S3SnapshotKey key: usageMap.keySet()) {
			String critVal = getAttributeValue(crit, key);
			String groupVal = getAttributeValue(groupByCrit, key);
			S3ReportLineKey lineKey = new S3ReportLineKey(critVal, groupVal);
			if (!reportLineMap.containsKey(lineKey)) {
				reportLineMap.put(lineKey, new S3ReportLine(lineKey,
						new S3UsageSummary(), displayUnits));
			}
			S3ReportLine reportLine = reportLineMap.get(lineKey);
			S3UsageSummary summary = usageMap.get(key);
			reportLine.addUsage(summary);
		}

		final List<S3ReportLine> results = new ArrayList<S3ReportLine>();
		for (S3ReportLineKey lineKey: reportLineMap.keySet()) {
			results.add(reportLineMap.get(lineKey));
		}
		
		return results;
	}

	
	private static String getAttributeValue(GroupByCriterion criterion,
			S3SnapshotKey key)
	{
		switch (criterion) {
			case ACCOUNT:
				return key.getAccountId();
			case USER:
				return key.getOwnerId();
			default:
				return key.getOwnerId();
		}
	}
	
	
}
