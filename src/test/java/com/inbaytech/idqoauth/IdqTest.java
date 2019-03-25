package com.inbaytech.idqoauth;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;

/**
  Copyright 2019 inBay Technologies Inc.

  The contents of this file are subject to the terms of the Common Development and
  Distribution License. You may not use this file except in compliance with the License.
  See the License for the specific language governing permission and limitations under the License.
 
  A copy of the License should have been provided in the LICENSE file in the root of this repository.
  If one was not, then one may be requested by contacting inBay Technologies: https://www.inbaytech.com/contact-us
*/

public class IdqTest {

	@Test
	public void testGetState_whenValid() {
		String idq_state = IdqOauth.getState();
		assertNotNull(idq_state);
	}

}
