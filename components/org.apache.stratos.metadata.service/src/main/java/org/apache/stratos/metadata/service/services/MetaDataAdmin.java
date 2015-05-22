/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.stratos.metadata.service.services;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.stratos.metadata.service.annotation.AuthorizationAction;
import org.apache.stratos.metadata.service.definition.Property;
import org.apache.stratos.metadata.service.exception.RestAPIException;
import org.apache.stratos.metadata.service.registry.DataRegistryFactory;
import org.apache.stratos.metadata.service.registry.DataStore;
import org.apache.stratos.metadata.service.util.ConfUtil;
import org.wso2.carbon.registry.api.RegistryException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("/")
public class MetaDataAdmin {
    private static Log log = LogFactory.getLog(MetaDataAdmin.class);
    @Context
    UriInfo uriInfo;
    private DataStore registry;

    /**
     * Meta data admin configuration loading
     */
    public MetaDataAdmin() {
        XMLConfiguration conf = ConfUtil.getInstance(null).getConfiguration();
        String DEFAULT_REG_TYPE = "carbon";
        String registryType = conf.getString("metadataservice.govenanceregistrytype", DEFAULT_REG_TYPE);
        registry = DataRegistryFactory.getDataStore(registryType);
    }

    @GET
    @Path("/application/{application_id}/properties")
    @Produces("application/json")
    @Consumes("application/json")
    @AuthorizationAction("/permission/protected/manage/monitor/tenants")
    public Response getApplicationProperties(@PathParam("application_id") String applicationId)
            throws RestAPIException {

        List<Property> properties;
        Property[] propertiesArr = null;
        try {
            properties = registry
                    .getApplicationProperties(applicationId);
            if (properties != null) {
                propertiesArr = new Property[properties.size()];
                propertiesArr = properties.toArray(propertiesArr);
            }
        } catch (RegistryException e) {
            String msg = "Error occurred while getting properties ";
            log.error(msg, e);
            throw new RestAPIException(msg, e);
        }

        Response.ResponseBuilder rb;
        if (propertiesArr == null) {
            rb = Response.status(Response.Status.NOT_FOUND);
        } else {
            rb = Response.ok().entity(propertiesArr);
        }
        return rb.build();
    }

    @GET
    @Path("/application/{application_id}/cluster/{cluster_id}/properties")
    @Produces("application/json")
    @Consumes("application/json")
    @AuthorizationAction("/permission/protected/manage/monitor/tenants")
    public Response getClusterProperties(@PathParam("application_id") String applicationId, @PathParam("cluster_id") String clusterId) throws RestAPIException {

        List<Property> properties;
        Property[] propertiesArr = null;
        try {
            properties = registry
                    .getClusterProperties(applicationId, clusterId);
            if (properties != null) {
                propertiesArr = new Property[properties.size()];
                propertiesArr = properties.toArray(propertiesArr);
            }
        } catch (RegistryException e) {
            String msg = "Error occurred while getting properties ";
            log.error(msg, e);
            throw new RestAPIException(msg, e);
        }

        Response.ResponseBuilder rb;
        if (propertiesArr == null) {
            rb = Response.status(Response.Status.NOT_FOUND);
        } else {
            rb = Response.ok().entity(propertiesArr);
        }
        return rb.build();
    }

    @GET
    @Path("/application/{application_id}/properties/{property_name}")
    @Produces("application/json")
    @Consumes("application/json")
    @AuthorizationAction("/permission/protected/manage/monitor/tenants")
    public Response getApplicationProperty(@PathParam("application_id") String applicationId,
                                           @PathParam("property_name") String propertyName)
            throws RestAPIException {
        List<Property> properties;


        Property property = null;
        try {
            properties = registry.getApplicationProperties(applicationId);
            if (properties == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            for (Property p : properties) {
                if (propertyName.equals(p.getKey())) {
                    property = p;
                    break;
                }
            }
        } catch (RegistryException e) {
            String msg = "Error occurred while getting property";
            log.error(msg, e);
            throw new RestAPIException(msg, e);
        }

        Response.ResponseBuilder rb;
        if (property == null) {
            rb = Response.status(Response.Status.NOT_FOUND);
        } else {
            rb = Response.ok().entity(property);
        }
        return rb.build();
    }

    @GET
    @Path("/application/{application_id}/cluster/{cluster_id}/properties/{property_name}")
    @Produces("application/json")
    @Consumes("application/json")
    @AuthorizationAction("/permission/protected/manage/monitor/tenants")
    public Response getClusterProperty(@PathParam("application_id") String applicationId, @PathParam("cluster_id") String clusterId, @PathParam("property_name") String propertyName) throws RestAPIException {
        List<Property> properties;

        Property property = null;
        try {
            properties = registry.getClusterProperties(applicationId, clusterId);
            if (properties == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            for (Property p : properties) {
                if (propertyName.equals(p.getKey())) {
                    property = p;
                    break;
                }
            }
        } catch (RegistryException e) {
            String msg = "Error occurred while getting property";
            log.error(msg, e);
            throw new RestAPIException(msg, e);
        }

        Response.ResponseBuilder rb;
        if (property == null) {
            rb = Response.status(Response.Status.NOT_FOUND);
        } else {
            rb = Response.ok().entity(property);
        }
        return rb.build();
    }

    @POST
    @Path("application/{application_id}/properties")
    @Produces("application/json")
    @Consumes("application/json")
    @AuthorizationAction("/permission/protected/manage/monitor/tenants")
    public Response addPropertyToApplication(@PathParam("application_id") String applicationId,
                                               Property property)
            throws RestAPIException {
        URI url = uriInfo.getAbsolutePathBuilder().path(applicationId).build();

        try {
            registry.addPropertyToApplication(applicationId, property);
        } catch (RegistryException e) {
            String msg = "Error occurred while adding properties ";
            log.error(msg, e);
            throw new RestAPIException(msg, e);
        }
        return Response.created(url).build();
    }

    @POST
    @Path("application/{application_id}/cluster/{cluster_id}/properties")
    @Produces("application/json")
    @Consumes("application/json")
    @AuthorizationAction("/permission/protected/manage/monitor/tenants")
    public Response addPropertyToCluster(@PathParam("application_id") String applicationId,
                                         @PathParam("cluster_id") String clusterId, Property property)
            throws RestAPIException {
        URI url = uriInfo.getAbsolutePathBuilder().path(applicationId + "/" + clusterId).build();

        try {
            registry.addPropertyToCluster(applicationId, clusterId, property);
        } catch (RegistryException e) {
            String msg = "Error occurred while adding properties ";
            log.error(msg, e);
            throw new RestAPIException(msg, e);
        }

        return Response.created(url).build();
    }

    @DELETE
    @Path("application/{application_id}")
    @Produces("application/json")
    @Consumes("application/json")
    @AuthorizationAction("/permission/protected/manage/monitor/tenants")
    public Response deleteApplicationProperties(@PathParam("application_id") String applicationId)
            throws RestAPIException {

        try {
            boolean deleted = registry.deleteApplication(applicationId);
            if (!deleted) {
                log.warn(String.format(
                        "Either no metadata is associated with given appId %s Or resources could not be deleted",
                        applicationId));
            }
        } catch (RegistryException e) {
            String msg = "Resource attached with appId could not be deleted";
            log.error(msg, e);
            throw new RestAPIException(msg, e);
        }

        return Response.ok().build();
    }

    @DELETE
    @Path("application/{application_id}/properties/{property_name}/value/{value}")
    @Produces("application/json")
    @Consumes("application/json")
    @AuthorizationAction("/permission/protected/manage/monitor/tenants")
    public Response deleteApplicationPropertValue(@PathParam("application_id") String applicationId, @PathParam("property_name") String propertyName,
                                                  @PathParam("value") String propertyValue                      )
            throws RestAPIException {

        try {
            registry.removePropertyFromApplication(applicationId, propertyName, propertyValue);
        } catch (RegistryException e) {
            String msg = "Resource value deletion failed ";
            log.error(msg, e);
            throw new RestAPIException(msg, e);
        }

        return Response.ok().build();
    }

}