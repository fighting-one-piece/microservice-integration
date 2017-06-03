package org.cisiondata.modules.producer;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderController {

	@Autowired
	private DiscoveryClient discoveryClient = null;

	@ResponseBody
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public Integer add(int a, int b) {
		return a + b;
	}
	
	@ResponseBody
	@RequestMapping(value = "/minus", method = RequestMethod.GET)
	public Integer minus(int a, int b) {
		return a - b;
	}

	@RequestMapping("/service-instances/{applicationName}")
	public List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String applicationName) {
		return this.discoveryClient.getInstances(applicationName);
	}

	@RequestMapping("/discovery")
	public String doDiscoveryService() {
		StringBuilder sb = new StringBuilder();
		List<String> serviceIds = discoveryClient.getServices();
		if (!CollectionUtils.isEmpty(serviceIds)) {
			for (String serviceId : serviceIds) {
				List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceId);
				if (!CollectionUtils.isEmpty(serviceInstances)) {
					for (ServiceInstance si : serviceInstances) {
						sb.append("[serviceId=" + si.getServiceId() + " host=" + si.getHost() + " port=" + si.getPort()
								+ " uri=" + si.getUri() + " metadata=" + si.getMetadata() + "]");
					}
				} else {
					sb.append("no service.");
				}
			}
		}
		return sb.toString();
	}

	@RequestMapping("/registered")
	public String getRegistered() {
		List<ServiceInstance> list = discoveryClient.getInstances("STORES");
		System.out.println(discoveryClient.getLocalServiceInstance());
		System.out.println("services size: " + discoveryClient.getServices().size());
		for (String s : discoveryClient.getServices()) {
			List<ServiceInstance> serviceInstances = discoveryClient.getInstances(s);
			for (ServiceInstance si : serviceInstances) {
				System.out.println("    services: " + s + " : getHost()=" + si.getHost());
				System.out.println("    services: " + s + " : getPort()=" + si.getPort());
				System.out.println("    services: " + s + " : getServiceId()=" + si.getServiceId());
				System.out.println("    services: " + s + " : getUri()=" + si.getUri());
				System.out.println("    services: " + s + " : getMetadata()=" + si.getMetadata());
			}
		}
		System.out.println(list.size());
		if (list != null && list.size() > 0) {
			System.out.println(list.get(0).getUri());
		}
		return null;
	}

}
