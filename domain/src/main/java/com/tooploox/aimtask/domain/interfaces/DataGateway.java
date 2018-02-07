package com.tooploox.aimtask.domain.interfaces;

import com.tooploox.aimtask.domain.entity.OnAirInfo;
import com.tooploox.aimtask.domain.entity.common.Result;

/**
 * DataGateway interface defines access to a remote source of the data.
 */
public interface DataGateway {

    Result<OnAirInfo> fetchStationInfo();
}
