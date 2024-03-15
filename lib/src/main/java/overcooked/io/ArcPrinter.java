package overcooked.io;

import overcooked.analysis.Arc;

class ArcPrinter {
  static String printArc(Arc arc) {
    return String.format("%s.%s(%s)",
        arc.getActionPerformerId(),
        arc.getLabel(),
        arc.getActionReceiverId() == null ? "" :
            arc.getActionReceiverId());
  }
}
