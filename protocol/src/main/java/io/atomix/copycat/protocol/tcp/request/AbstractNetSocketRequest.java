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
package io.atomix.copycat.protocol.tcp.request;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.atomix.copycat.protocol.request.ProtocolRequest;

import java.util.Objects;

/**
 * Base request for all client requests.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public abstract class AbstractNetSocketRequest implements NetSocketRequest {
  @JsonProperty("id")
  protected final long id;

  protected AbstractNetSocketRequest(long id) {
    this.id = id;
  }

  @Override
  @JsonGetter("id")
  public long id() {
    return id;
  }

  /**
   * Abstract request builder.
   *
   * @param <T> The builder type.
   * @param <U> The request type.
   */
  protected static abstract class Builder<T extends ProtocolRequest.Builder<T, U>, U extends ProtocolRequest> implements ProtocolRequest.Builder<T, U> {
    protected final long id;

    /**
     * @throws NullPointerException if {@code pool} or {@code factory} are null
     */
    protected Builder(long id) {
      this.id = id;
    }

    @Override
    public int hashCode() {
      return Objects.hash(id);
    }

    @Override
    public boolean equals(Object object) {
      return getClass().isAssignableFrom(object.getClass()) && ((Builder) object).id == id;
    }

    @Override
    public String toString() {
      return String.format("%s[request=%s]", getClass().getCanonicalName(), id);
    }
  }

  /**
   * Request serializer.
   */
  public static abstract class Serializer<T extends AbstractNetSocketRequest> extends com.esotericsoftware.kryo.Serializer<T> {
  }
}
