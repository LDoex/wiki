package com.oyyk.wiki.mapper;

import com.oyyk.wiki.resp.StatisticResp;

import java.util.List;

public interface EbookSnapshotMapperCust {
    public void genSnapshot();
    public List<StatisticResp> getStatistic();
}
