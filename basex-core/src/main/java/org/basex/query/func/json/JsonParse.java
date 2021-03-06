package org.basex.query.func.json;

import org.basex.build.json.*;
import org.basex.io.parse.json.*;
import org.basex.query.*;
import org.basex.query.func.*;
import org.basex.query.value.item.*;
import org.basex.util.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-17, BSD License
 * @author Christian Gruen
 */
public final class JsonParse extends StandardFunc {
  @Override
  public Item item(final QueryContext qc, final InputInfo ii) throws QueryException {
    final byte[] input = toToken(exprs[0], qc);
    final JsonParserOptions opts = toOptions(1, new JsonParserOptions(), qc);
    try {
      return JsonConverter.get(opts).convert(input, null);
    } catch(final QueryIOException ex) {
      throw ex.getCause(info);
    }
  }
}
