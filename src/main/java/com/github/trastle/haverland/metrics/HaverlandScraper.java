package com.github.trastle.haverland.metrics;

import com.github.trastle.haverlandsmartwaveclient.HaverlandSmartwaveClient;
import com.github.trastle.haverlandsmartwaveclient.HaverlandSmartwaveDeviceClient;
import com.github.trastle.haverlandsmartwaveclient.model.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.prometheus.client.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Scrape the Haverland REST API for metrics
 */
public class HaverlandScraper implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HaverlandScraper.class);

    private static final Counter successCounter =
            Counter.build("heater_scrape_success_counter", "Number of successful scrapes").create().register();
    private static final Counter failureCounter =
            Counter.build("heater_scrape_failure_counter", "Number of failed scrapes").create().register();
    private static final Counter totalCounter =
            Counter.build("heater_scrape_total_counter", "Number of total scrapes").create().register();

    private static final String CACHE_KEY_DEVICE_CLIENT = "CACHE_KEY_DEVICE_CLIENT";
    private static final String CACHE_KEY_HEATER_TOOL_DEVICE = "CACHE_KEY_HEATER_TOOL_DEVICE";
    private static final String CACHE_KEY_HEATER_TOOL_NODES = "CACHE_KEY_HEATER_TOOL_NODES";

    private final String username;
    private final String password;

    // Store what we get from the API for one day, then refresh it.
    Cache<String, Object> cache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.DAYS).maximumSize(10).build();
    private Map<Node, NodeMetrics> nodeMetrics = new HashMap<>();

    public HaverlandScraper(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void run() {
        try {
            for (Node node : getNodes()) {
                HeaterStatus status = getDeviceClient().getHeaterStatus(getDevice().getDeviceId(), node.getAddress());
                nodeMetrics.get(node).update(status);
            }
            successCounter.inc();
        } catch (Exception e) {
            LOGGER.error("Scrape Failed", e);
            failureCounter.inc();
        } finally {
            totalCounter.inc();
        }
    }

    private HaverlandSmartwaveDeviceClient getDeviceClient() {
        HaverlandSmartwaveDeviceClient deviceClient =
                (HaverlandSmartwaveDeviceClient) cache.getIfPresent(CACHE_KEY_DEVICE_CLIENT);

        if (deviceClient == null) {
            final HaverlandSmartwaveClient authClient = new HaverlandSmartwaveClient();
            final AuthResponse authResponse = authClient.authenticate(username, password);
            LOGGER.info("Fetched AuthResponse: " + authResponse);
            deviceClient = authClient.getServiceProxy(authResponse);
            cache.put(CACHE_KEY_DEVICE_CLIENT, deviceClient);
        }

        return deviceClient;
    }

    private Device getDevice() {
        Device device = (Device) cache.getIfPresent(CACHE_KEY_HEATER_TOOL_DEVICE);

        if (device == null) {
            final HaverlandSmartwaveDeviceClient client = getDeviceClient();
            device = client.getDevices().getDevices().get(0);
            LOGGER.info("Fetched Device: " + device);
            cache.put(CACHE_KEY_HEATER_TOOL_DEVICE, device);
        }

        return device;
    }

    private List<Node> getNodes() {
        List<Node> nodes = (List<Node>) cache.getIfPresent(CACHE_KEY_HEATER_TOOL_NODES);

        if (nodes == null) {
            final HaverlandSmartwaveDeviceClient client = getDeviceClient();
            final Device device = getDevice();
            final NodesResponse nodesResponse = client.getNodes(device.getDeviceId());
            nodes = nodesResponse.getNodes();
            LOGGER.info("Fetched Nodes: " + nodes);
            cache.put(CACHE_KEY_HEATER_TOOL_NODES, nodes);

            for (Node node : nodes) {
                this.nodeMetrics.put(node, new NodeMetrics(device, node));
            }
        }

        return nodes;
    }

}
