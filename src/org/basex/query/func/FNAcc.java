package org.basex.query.func;

import static org.basex.util.Token.*;
import org.basex.query.QueryContext;
import org.basex.query.QueryException;
import org.basex.query.expr.Expr;
import org.basex.query.item.Dbl;
import org.basex.query.item.Item;
import org.basex.query.item.Itr;
import org.basex.query.item.QNm;
import org.basex.query.item.Str;
import org.basex.query.item.Type;
import org.basex.query.iter.Iter;

/**
 * Accessor functions.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Christian Gruen
 */
final class FNAcc extends Fun {
  @Override
  public Item atomic(final QueryContext ctx) throws QueryException {
    final Expr e = expr.length != 0 ? expr[0] : checkCtx(ctx);

    switch(func) {
      case POS:
        return Itr.get(ctx.pos);
      case LAST:
        return Itr.get(ctx.size);
      case STRING:
        Item it = e.atomic(ctx);
        return it == null ? Str.ZERO : it.s() && !it.u() ? it :
          Str.get(it.str());
      case NUMBER:
        final Iter ir = ctx.iter(e);
        it = ir.next();
        return it == null || ir.next() != null ? Dbl.NAN : number(it);
      case STRLEN:
        return Itr.get(len(checkStr(e, ctx)));
      case NORM:
        return Str.get(norm(checkStr(e, ctx)));
      case URIQNAME:
        it = e.atomic(ctx);
        if(it == null) return null;
        return ((QNm) check(it, Type.QNM)).uri;
      default:
        return super.atomic(ctx);
    }
  }
  
  @Override
  public Expr c(final QueryContext ctx) throws QueryException {
    if(expr.length == 0) return this;
    final Item it = expr[0].i() ? (Item) expr[0] : null;
    
    switch(func) {
      case STRING:
        return it != null ? atomic(ctx) : this;
      case NUMBER:
        return expr[0].e() || it != null ? atomic(ctx) : this;
      default:
        return this;
    }
  }
  
  /**
   * Converts the specified item to a double.
   * @param it input item
   * @return double iterator
   */
  private Item number(final Item it) {
    if(it.type == Type.DBL) return it;

    try {
      if(it.type != Type.URI && (it.s() || it.n() || it.u()))
        return Dbl.get(it.dbl());
    } catch(final QueryException e) { }

    return Dbl.get(Double.NaN);
  }

  @Override
  public boolean uses(final Use use, final QueryContext ctx) {
    return (use == Use.POS || use == Use.VAR) &&
      (func == FunDef.POS || func == FunDef.LAST) || super.uses(use, ctx);
  }
}
