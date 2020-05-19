/**
 * © 2020 Copyright Amadeus Unauthorised use and disclosure strictly forbidden.
 */
package tr.com.ogedik.authentication.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author orkun.gedik
 */
@Getter
@Setter
@NoArgsConstructor
public class AbstractAuthenticationModel implements Serializable {

    private Long resourceId;

    private MetaInformation metaInformation;
}