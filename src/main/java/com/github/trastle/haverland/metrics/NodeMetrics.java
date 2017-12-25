package com.github.trastle.haverland.metrics;

import com.github.trastle.haverlandsmartwaveclient.model.Device;
import com.github.trastle.haverlandsmartwaveclient.model.HeaterStatus;
import com.github.trastle.haverlandsmartwaveclient.model.Node;
import io.prometheus.client.Gauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Report the data about a Node to Prometheus.
 */
public class NodeMetrics {

    private static final Logger LOGGER = LoggerFactory.getLogger(HaverlandScraper.class);

    private static final Gauge currentTempGauge =
            Gauge.build("heater_temperature_current", "Current heater temperature")
                    .labelNames("device", "node").create().register();

    private static final Gauge targetTempGauge =
            Gauge.build("heater_temperature_target", "Target heater temperature")
                    .labelNames("device", "node").create().register();

    private static final Gauge isActiveGauge =
            Gauge.build("heater_is_active", "Is the heater currently heating")
                    .labelNames("device", "node").create().register();

    private static final Gauge isOnGauge =
            Gauge.build("heater_is_on", "Is the heater currently turned on")
                    .labelNames("device", "node").create().register();

    private static final Gauge isLockedGauge =
            Gauge.build("heater_is_locked", "Is the heater currently locked")
                    .labelNames("device", "node").create().register();

    private final String node;
    private final String device;

    public NodeMetrics(Device device, Node node) {
        this.device = removeAllNonAlphaNumeric(device.getName());
        this.node = removeAllNonAlphaNumeric(node.getName());
    }

    public void update(HeaterStatus status) {
        LOGGER.debug("{} {} {}", device, node, status);
        currentTempGauge.labels(device, node).set(status.getMeasuredTemp());
        targetTempGauge.labels(device, node).set(status.calculateTargetTemperature());
        isActiveGauge.labels(device, node).set(boolToLong(status.isActive()));
        isOnGauge.labels(device, node).set(boolToLong(status.isOn()));
        isLockedGauge.labels(device, node).set(boolToLong(status.isLocked()));
    }

    private String removeAllNonAlphaNumeric(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "");
    }

    private long boolToLong(boolean bool) {
        if (bool) {
            return 1;
        } else return 0;
    }
}
