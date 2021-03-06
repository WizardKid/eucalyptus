/*************************************************************************
 * Copyright 2009-2015 Ent. Services Development Corporation LP
 *
 * Redistribution and use of this software in source and binary forms,
 * with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer
 *   in the documentation and/or other materials provided with the
 *   distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ************************************************************************/
package com.eucalyptus.compute.policy;

import com.eucalyptus.auth.AuthException;
import com.eucalyptus.auth.policy.PolicySpec;
import com.eucalyptus.auth.policy.key.KeyUtils;
import com.eucalyptus.auth.policy.key.PolicyKey;
import com.eucalyptus.auth.policy.key.QuotaKey;
import com.eucalyptus.auth.principal.AccountFullName;
import com.eucalyptus.auth.principal.PolicyScope;
import com.eucalyptus.auth.principal.UserFullName;
import com.eucalyptus.cluster.common.Cluster;
import com.eucalyptus.cluster.Clusters;
import com.eucalyptus.compute.common.CloudMetadataLimitedType;
import com.eucalyptus.entities.Entities;
import com.eucalyptus.entities.TransactionResource;
import com.eucalyptus.auth.principal.OwnerFullName;
import com.eucalyptus.util.RestrictedTypes;
import com.eucalyptus.compute.common.internal.vm.VmInstance;
import com.google.common.base.Function;
import net.sf.json.JSONException;
import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by ethomas on 3/8/15.
 */
@PolicyKey( MemoryTotalSizeQuotaKey.KEY )
public class MemoryTotalSizeQuotaKey extends QuotaKey {

  public static final String KEY = "ec2:quota-memorytotalsize";
  public static final String POLICY_RESOURCE_TYPE = CloudMetadataLimitedType.MemoryMetadata.POLICY_RESOURCE_TYPE;
  @Override
  public void validateValueType( String value ) throws JSONException {
    KeyUtils.validateIntegerValue(value, KEY);
  }

  @Override
  public boolean canApply( String action, String resourceType) {
    if ( PolicySpec.qualifiedName( PolicySpec.VENDOR_EC2, PolicySpec.EC2_RUNINSTANCES ).equals( action )
      && PolicySpec.qualifiedName( PolicySpec.VENDOR_EC2, POLICY_RESOURCE_TYPE).equals(resourceType) ) {
      return true;
    }
    if ( PolicySpec.qualifiedName( PolicySpec.VENDOR_EC2, PolicySpec.EC2_STARTINSTANCES ).equals( action )
      && PolicySpec.qualifiedName( PolicySpec.VENDOR_EC2, POLICY_RESOURCE_TYPE).equals(resourceType) ) {
      return true;
    }
    return false;
  }

  @Override
  public String value( PolicyScope scope, String id, String resource, Long quantity ) throws AuthException {
    switch ( scope ) {
      case Account:
        return Long.toString( RestrictedTypes.usageMetricFunction(CloudMetadataLimitedType.MemoryMetadata.class).apply( AccountFullName.getInstance(id) ) + quantity );
      case Group:
        return NOT_SUPPORTED;
      case User:
        return Long.toString( RestrictedTypes.usageMetricFunction(CloudMetadataLimitedType.MemoryMetadata.class).apply( UserFullName.getInstance(id) ) + quantity );
    }
    throw new AuthException( "Invalid scope" );
  }

  @RestrictedTypes.UsageMetricFunction( CloudMetadataLimitedType.MemoryMetadata.class )
  public enum MeasureMemoryAmount implements Function<OwnerFullName, Long> {
    INSTANCE;

    @Override
    public Long apply( final OwnerFullName ownerFullName ) {
      return
        measureFromPersistentInstances(ownerFullName) +
          measureFromPendingInstances(ownerFullName);
    }

    private long measureFromPersistentInstances( final OwnerFullName ownerFullName ) {
      long numMemorys = 0L;
      try ( TransactionResource tx = Entities.transactionFor( VmInstance.class ) ){
        Criteria criteria = Entities.createCriteria(VmInstance.class)
          .add(Example.create(VmInstance.named(ownerFullName, null)))
          .add(Restrictions.not(Restrictions.in("state", VmInstance.VmStateSet.TORNDOWN.array())));
        List<VmInstance> result = (List<VmInstance>) criteria.list();
        if (result != null) {
          for (VmInstance instance : result) {
            numMemorys += instance.getVmType().getMemory();
          }
        }
      }
      return numMemorys;
    }

    private long measureFromPendingInstances( final OwnerFullName ownerFullName ) {
      long pending = 0;
      for ( final Cluster cluster : Clusters.list( ) ) {
        pending += cluster.getNodeState( ).measureUncommittedPendingInstanceMemoryAmount(ownerFullName);
      }
      return pending;
    }
  }

}
