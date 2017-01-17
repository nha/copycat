/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package io.atomix.copycat.protocol.tcp.response;

import io.atomix.catalyst.util.Assert;
import io.atomix.copycat.error.CopycatError;
import io.atomix.copycat.protocol.response.OperationResponse;

import java.util.Objects;

/**
 * Base client operation response.
 * <p>
 * All operation responses are sent with a {@link #result()} and the {@link #index()} (or index) of the state
 * machine at the point at which the operation was evaluated. The version allows clients to ensure state progresses
 * monotonically when switching servers by providing the state machine version in future operation requests.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public abstract class NetSocketOperationResponse extends NetSocketSessionResponse implements OperationResponse {
  protected final long index;
  protected final long eventIndex;
  protected final Object result;

  protected NetSocketOperationResponse(long id, Status status, CopycatError error, long index, long eventIndex, Object result) {
    super(id, status, error);
    this.index = index;
    this.eventIndex = eventIndex;
    this.result = result;
  }

  @Override
  public long index() {
    return index;
  }

  @Override
  public long eventIndex() {
    return eventIndex;
  }

  @Override
  public Object result() {
    return result;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getClass(), status, result);
  }

  @Override
  public boolean equals(Object object) {
    if (getClass().isAssignableFrom(object.getClass())) {
      NetSocketOperationResponse response = (NetSocketOperationResponse) object;
      return response.status == status
        && response.index == index
        && response.eventIndex == eventIndex
        && ((response.result == null && result == null)
        || response.result != null && result != null && response.result.equals(result));
    }
    return false;
  }

  @Override
  public String toString() {
    return String.format("%s[status=%s, index=%d, eventIndex=%d, result=%s]", getClass().getSimpleName(), status, index, eventIndex, result);
  }

  /**
   * Operation response builder.
   */
  public static abstract class Builder<T extends OperationResponse.Builder<T, U>, U extends OperationResponse> extends NetSocketSessionResponse.Builder<T, U> implements OperationResponse.Builder<T, U> {
    protected long index;
    protected long eventIndex;
    protected Object result;

    public Builder(long id) {
      super(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T withIndex(long index) {
      this.index = Assert.argNot(index, index < 0, "index must be positive");
      return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T withEventIndex(long eventIndex) {
      this.eventIndex = Assert.argNot(eventIndex, eventIndex < 0, "eventIndex must be positive");
      return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T withResult(Object result) {
      this.result = result;
      return (T) this;
    }
  }

  /**
   * Operation response serializer.
   */
  public static abstract class Serializer<T extends NetSocketOperationResponse> extends NetSocketSessionResponse.Serializer<T> {
  }
}
