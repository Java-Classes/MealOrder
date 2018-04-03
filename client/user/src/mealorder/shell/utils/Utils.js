class Utils {
    static compareObjects(o1, o2) {
        for (let p in o1) {
            if (o1.hasOwnProperty(p)) {
                if (o1[p] !== o2[p]) {
                    return false;
                }
            }
        }
        for (let p in o2) {
            if (o2.hasOwnProperty(p)) {
                if (o1[p] !== o2[p]) {
                    return false;
                }
            }
        }
        return true;
    };
}
