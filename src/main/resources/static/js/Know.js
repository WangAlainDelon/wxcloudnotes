﻿!
function() {
    var e = null; (function() {
        function t(e) {
            function t() {
                try {
                    o.doScroll("left")
                } catch(e) {
                    return r(t, 50),
                    void 0
                }
                i("poll")
            }
            function i(t) {
                "readystatechange" == t.type && "complete" != s.readyState || (("load" == t.type ? n: s)[u](d + t.type, i, !1), l || !(l = !0)) || e.call(n, t.type || t)
            }
            var a = s.addEventListener,
            l = !1,
            h = !0,
            c = a ? "addEventListener": "attachEvent",
            u = a ? "removeEventListener": "detachEvent",
            d = a ? "": "on";
            if ("complete" == s.readyState) e.call(n, "lazy");
            else {
                if (s.createEventObject && o.doScroll) {
                    try {
                        h = !n.frameElement
                    } catch(p) {}
                    h && t()
                }
                s[c](d + "DOMContentLoaded", i, !1),
                s[c](d + "readystatechange", i, !1),
                n[c](d + "load", i, !1)
            }
        }
        function i() {
            p && t(function() {
                var e = g.length;
                v(e ?
                function() {
                    for (var t = 0; e > t; ++t)(function(e) {
                        r(function() {
                            n.exports[g[e]].apply(n, arguments)
                        },
                        0)
                    })(t)
                }: void 0)
            })
        }
        for (var n = window,
        r = n.setTimeout,
        s = document,
        o = s.documentElement,
        a = s.head || s.getElementsByTagName("head")[0] || o, l = "", h = s.scripts, c = h.length; --c >= 0;) {
            var u = h[c],
            d = u.src.match(/^[^#?]*\/run_prettify\.js(\?[^#]*)?(?:#.*)?$/);
            if (d) {
                l = d[1] || "",
                u.parentNode.removeChild(u);
                break
            }
        }
        var p = !0,
        f = [],
        m = [],
        g = [];
        for (l.replace(/[&?]([^&=]+)=([^&]+)/g,
        function(e, t, i) {
            i = decodeURIComponent(i),
            t = decodeURIComponent(t),
            "autorun" == t ? p = !/^[0fn]/i.test(i) : "lang" == t ? f.push(i) : "skin" == t ? m.push(i) : "callback" == t && g.push(i)
        }), c = 0, l = f.length; l > c; ++c)(function() {
            var t = s.createElement("script");
            t.onload = t.onerror = t.onreadystatechange = function() { ! t || t.readyState && !/loaded|complete/.test(t.readyState) || (t.onerror = t.onload = t.onreadystatechange = e, --y, y || r(i, 0), t.parentNode && t.parentNode.removeChild(t), t = e)
            },
            t.type = "text/javascript",
            t.src = "https://google-code-prettify.googlecode.com/svn/loader/lang-" + encodeURIComponent(f[c]) + ".js",
            a.insertBefore(t, a.firstChild)
        })(f[c]);
        for (var y = f.length,
        h = [], c = 0, l = m.length; l > c; ++c) h.push("https://google-code-prettify.googlecode.com/svn/loader/skins/" + encodeURIComponent(m[c]) + ".css");
        h.push("../../static/css/prettify.min.css"),
        function(e) {
            function t(n) {
                if (n !== i) {
                    var r = s.createElement("link");
                    r.rel = "stylesheet",
                    r.type = "text/css",
                    i > n + 1 && (r.error = r.onerror = function() {
                        t(n + 1)
                    }),
                    r.href = e[n],
                    a.appendChild(r)
                }
            }
            var i = e.length;
            t(0)
        } (h);
        var v = function() {
            window.PR_SHOULD_USE_CONTINUATION = !0;
            var t;
            return function() {
                function i(e) {
                    function t(e) {
                        var t = e.charCodeAt(0);
                        if (92 !== t) return t;
                        var i = e.charAt(1);
                        return (t = u[i]) ? t: i >= "0" && "7" >= i ? parseInt(e.substring(1), 8) : "u" === i || "x" === i ? parseInt(e.substring(2), 16) : e.charCodeAt(1)
                    }
                    function i(e) {
                        return 32 > e ? (16 > e ? "\\x0": "\\x") + e.toString(16) : (e = String.fromCharCode(e), "\\" === e || "-" === e || "]" === e || "^" === e ? "\\" + e: e)
                    }
                    function n(e) {
                        var n = e.substring(1, e.length - 1).match(/\\u[\dA-Fa-f]{4}|\\x[\dA-Fa-f]{2}|\\[0-3][0-7]{0,2}|\\[0-7]{1,2}|\\[\S\s]|[^\\]/g),
                        e = [],
                        r = "^" === n[0],
                        s = ["["];
                        r && s.push("^");
                        for (var r = r ? 1 : 0, o = n.length; o > r; ++r) {
                            var a = n[r];
                            if (/\\[bdsw]/i.test(a)) s.push(a);
                            else {
                                var l, a = t(a);
                                o > r + 2 && "-" === n[r + 1] ? (l = t(n[r + 2]), r += 2) : l = a,
                                e.push([a, l]),
                                65 > l || a > 122 || (65 > l || a > 90 || e.push([32 | Math.max(65, a), 32 | Math.min(l, 90)]), 97 > l || a > 122 || e.push([ - 33 & Math.max(97, a), -33 & Math.min(l, 122)]))
                            }
                        }
                        for (e.sort(function(e, t) {
                            return e[0] - t[0] || t[1] - e[1]
                        }), n = [], o = [], r = 0; e.length > r; ++r) a = e[r],
                        a[0] <= o[1] + 1 ? o[1] = Math.max(o[1], a[1]) : n.push(o = a);
                        for (r = 0; n.length > r; ++r) a = n[r],
                        s.push(i(a[0])),
                        a[1] > a[0] && (a[1] + 1 > a[0] && s.push("-"), s.push(i(a[1])));
                        return s.push("]"),
                        s.join("")
                    }
                    function r(e) {
                        for (var t = e.source.match(/\[(?:[^\\\]]|\\[\S\s])*]|\\u[\dA-Fa-f]{4}|\\x[\dA-Fa-f]{2}|\\\d+|\\[^\dux]|\(\?[!:=]|[()^]|[^()[\\^]+/g), r = t.length, a = [], l = 0, h = 0; r > l; ++l) {
                            var c = t[l];
                            "(" === c ? ++h: "\\" === c.charAt(0) && (c = +c.substring(1)) && (h >= c ? a[c] = -1 : t[l] = i(c))
                        }
                        for (l = 1; a.length > l; ++l) - 1 === a[l] && (a[l] = ++s);
                        for (h = l = 0; r > l; ++l) c = t[l],
                        "(" === c ? (++h, a[h] || (t[l] = "(?:")) : "\\" === c.charAt(0) && (c = +c.substring(1)) && h >= c && (t[l] = "\\" + a[c]);
                        for (l = 0; r > l; ++l)"^" === t[l] && "^" !== t[l + 1] && (t[l] = "");
                        if (e.ignoreCase && o) for (l = 0; r > l; ++l) c = t[l],
                        e = c.charAt(0),
                        c.length >= 2 && "[" === e ? t[l] = n(c) : "\\" !== e && (t[l] = c.replace(/[A-Za-z]/g,
                        function(e) {
                            return e = e.charCodeAt(0),
                            "[" + String.fromCharCode( - 33 & e, 32 | e) + "]"
                        }));
                        return t.join("")
                    }
                    for (var s = 0,
                    o = !1,
                    a = !1,
                    l = 0,
                    h = e.length; h > l; ++l) {
                        var c = e[l];
                        if (c.ignoreCase) a = !0;
                        else if (/[a-z]/i.test(c.source.replace(/\\u[\da-f]{4}|\\x[\da-f]{2}|\\[^UXux]/gi, ""))) {
                            o = !0,
                            a = !1;
                            break
                        }
                    }
                    for (var u = {
                        b: 8,
                        t: 9,
                        n: 10,
                        v: 11,
                        f: 12,
                        r: 13
                    },
                    d = [], l = 0, h = e.length; h > l; ++l) {
                        if (c = e[l], c.global || c.multiline) throw Error("" + c);
                        d.push("(?:" + r(c) + ")")
                    }
                    return RegExp(d.join("|"), a ? "gi": "g")
                }
                function n(e, t) {
                    function i(e) {
                        var l = e.nodeType;
                        if (1 == l) {
                            if (!n.test(e.className)) {
                                for (l = e.firstChild; l; l = l.nextSibling) i(l);
                                l = e.nodeName.toLowerCase(),
                                ("br" === l || "li" === l) && (r[a] = "\n", o[a << 1] = s++, o[1 | a++<<1] = e)
                            }
                        } else(3 == l || 4 == l) && (l = e.nodeValue, l.length && (l = t ? l.replace(/\r\n?/g, "\n") : l.replace(/[\t\n\r ]+/g, " "), r[a] = l, o[a << 1] = s, s += l.length, o[1 | a++<<1] = e))
                    }
                    var n = /(?:^|\s)nocode(?:\s|$)/,
                    r = [],
                    s = 0,
                    o = [],
                    a = 0;
                    return i(e),
                    {
                        a: r.join("").replace(/\n$/, ""),
                        d: o
                    }
                }
                function s(e, t, i, n) {
                    t && (e = {
                        a: t,
                        e: e
                    },
                    i(e), n.push.apply(n, e.g))
                }
                function o(e) {
                    for (var t = void 0,
                    i = e.firstChild; i; i = i.nextSibling) var n = i.nodeType,
                    t = 1 === n ? t ? e: i: 3 === n ? w.test(i.nodeValue) ? e: t: t;
                    return t === e ? void 0 : t
                }
                function a(t, n) {
                    function r(e) {
                        for (var t = e.e,
                        i = [t, "pln"], h = 0, c = e.a.match(o) || [], d = {},
                        p = 0, f = c.length; f > p; ++p) {
                            var m, g = c[p],
                            y = d[g],
                            v = void 0;
                            if ("string" == typeof y) m = !1;
                            else {
                                var b = a[g.charAt(0)];
                                if (b) v = g.match(b[1]),
                                y = b[0];
                                else {
                                    for (m = 0; l > m; ++m) if (b = n[m], v = g.match(b[1])) {
                                        y = b[0];
                                        break
                                    }
                                    v || (y = "pln")
                                } ! (m = y.length >= 5 && "lang-" === y.substring(0, 5)) || v && "string" == typeof v[1] || (m = !1, y = "src"),
                                m || (d[g] = y)
                            }
                            if (b = h, h += g.length, m) {
                                m = v[1];
                                var L = g.indexOf(m),
                                x = L + m.length;
                                v[2] && (x = g.length - v[2].length, L = x - m.length),
                                y = y.substring(5),
                                s(t + b, g.substring(0, L), r, i),
                                s(t + b + L, m, u(y, m), i),
                                s(t + b + x, g.substring(x), r, i)
                            } else i.push(t + b, y)
                        }
                        e.g = i
                    }
                    var o, a = {}; (function() {
                        for (var r = t.concat(n), s = [], l = {},
                        h = 0, c = r.length; c > h; ++h) {
                            var u = r[h],
                            d = u[3];
                            if (d) for (var p = d.length; --p >= 0;) a[d.charAt(p)] = u;
                            u = u[1],
                            d = "" + u,
                            l.hasOwnProperty(d) || (s.push(u), l[d] = e)
                        }
                        s.push(/[\S\s]/),
                        o = i(s)
                    })();
                    var l = n.length;
                    return r
                }
                function l(t) {
                    var i = [],
                    n = [];
                    t.tripleQuotedStrings ? i.push(["str", /^(?:'''(?:[^'\\]|\\[\S\s]|''?(?=[^']))*(?:'''|$)|"""(?:[^"\\]|\\[\S\s]|""?(?=[^"]))*(?:"""|$)|'(?:[^'\\]|\\[\S\s])*(?:'|$)|"(?:[^"\\]|\\[\S\s])*(?:"|$))/, e, "'\""]) : t.multiLineStrings ? i.push(["str", /^(?:'(?:[^'\\]|\\[\S\s])*(?:'|$)|"(?:[^"\\]|\\[\S\s])*(?:"|$)|`(?:[^\\`]|\\[\S\s])*(?:`|$))/, e, "'\"`"]) : i.push(["str", /^(?:'(?:[^\n\r'\\]|\\.)*(?:'|$)|"(?:[^\n\r"\\]|\\.)*(?:"|$))/, e, "\"'"]),
                    t.verbatimStrings && n.push(["str", /^@"(?:[^"]|"")*(?:"|$)/, e]);
                    var r = t.hashComments;
                    if (r && (t.cStyleComments ? (r > 1 ? i.push(["com", /^#(?:##(?:[^#]|#(?!##))*(?:###|$)|.*)/, e, "#"]) : i.push(["com", /^#(?:(?:define|e(?:l|nd)if|else|error|ifn?def|include|line|pragma|undef|warning)\b|[^\n\r]*)/, e, "#"]), n.push(["str", /^<(?:(?:(?:\.\.\/)*|\/?)(?:[\w-]+(?:\/[\w-]+)+)?[\w-]+\.h(?:h|pp|\+\+)?|[a-z]\w*)>/, e])) : i.push(["com", /^#[^\n\r]*/, e, "#"])), t.cStyleComments && (n.push(["com", /^\/\/[^\n\r]*/, e]), n.push(["com", /^\/\*[\S\s]*?(?:\*\/|$)/, e])), r = t.regexLiterals) {
                        var s = (r = r > 1 ? "": "\n\r") ? ".": "[\\S\\s]";
                        n.push(["lang-regex", RegExp("^(?:^^\\.?|[+-]|[!=]=?=?|\\#|%=?|&&?=?|\\(|\\*=?|[+\\-]=|->|\\/=?|::?|<<?=?|>>?>?=?|,|;|\\?|@|\\[|~|{|\\^\\^?=?|\\|\\|?=?|break|case|continue|delete|do|else|finally|instanceof|return|throw|try|typeof)\\s*(" + ("/(?=[^/*" + r + "])(?:[^/\\x5B\\x5C" + r + "]|\\x5C" + s + "|\\x5B(?:[^\\x5C\\x5D" + r + "]|\\x5C" + s + ")*(?:\\x5D|$))+/") + ")")])
                    }
                    return (r = t.types) && n.push(["typ", r]),
                    r = ("" + t.keywords).replace(/^ | $/g, ""),
                    r.length && n.push(["kwd", RegExp("^(?:" + r.replace(/[\s,]+/g, "|") + ")\\b"), e]),
                    i.push(["pln", /^\s+/, e, " \r\n	?"]),
                    r = "^.[^\\s\\w.$@'\"`/\\\\]*",
                    t.regexLiterals && (r += "(?!s*/)"),
                    n.push(["lit", /^@[$_a-z][\w$@]*/i, e], ["typ", /^(?:[@_]?[A-Z]+[a-z][\w$@]*|\w+_t\b)/, e], ["pln", /^[$_a-z][\w$@]*/i, e], ["lit", /^(?:0x[\da-f]+|(?:\d(?:_\d+)*\d*(?:\.\d*)?|\.\d\+)(?:e[+-]?\d+)?)[a-z]*/i, e, "0123456789"], ["pln", /^\\[\S\s]?/, e], ["pun", RegExp(r), e]),
                    a(i, n)
                }
                function h(e, t, i) {
                    function n(e) {
                        var t = e.nodeType;
                        if (1 != t || s.test(e.className)) {
                            if ((3 == t || 4 == t) && i) {
                                var l = e.nodeValue,
                                h = l.match(o);
                                h && (t = l.substring(0, h.index), e.nodeValue = t, (l = l.substring(h.index + h[0].length)) && e.parentNode.insertBefore(a.createTextNode(l), e.nextSibling), r(e), t || e.parentNode.removeChild(e))
                            }
                        } else if ("br" === e.nodeName) r(e),
                        e.parentNode && e.parentNode.removeChild(e);
                        else for (e = e.firstChild; e; e = e.nextSibling) n(e)
                    }
                    function r(e) {
                        function t(e, i) {
                            var n = i ? e.cloneNode(!1) : e,
                            r = e.parentNode;
                            if (r) {
                                var r = t(r, 1),
                                s = e.nextSibling;
                                r.appendChild(n);
                                for (var o = s; o; o = s) s = o.nextSibling,
                                r.appendChild(o)
                            }
                            return n
                        }
                        for (; ! e.nextSibling;) if (e = e.parentNode, !e) return;
                        for (var i, e = t(e.nextSibling, 0); (i = e.parentNode) && 1 === i.nodeType;) e = i;
                        h.push(e)
                    }
                    for (var s = /(?:^|\s)nocode(?:\s|$)/,
                    o = /\r\n?|\n/,
                    a = e.ownerDocument,
                    l = a.createElement("li"); e.firstChild;) l.appendChild(e.firstChild);
                    for (var h = [l], c = 0; h.length > c; ++c) n(h[c]);
                    t === (0 | t) && h[0].setAttribute("value", t);
                    var u = a.createElement("ol");
                    u.className = "linenums";
                    for (var t = Math.max(0, 0 | t - 1) || 0, c = 0, d = h.length; d > c; ++c) l = h[c],
                    l.className = "L" + (c + t) % 10,
                    l.firstChild || l.appendChild(a.createTextNode("?")),
                    u.appendChild(l);
                    e.appendChild(u)
                }
                function c(e, t) {
                    for (var i = t.length; --i >= 0;) {
                        var n = t[i];
                        O.hasOwnProperty(n) ? p.console && console.warn("cannot override language handler %s", n) : O[n] = e
                    }
                }
                function u(e, t) {
                    return e && O.hasOwnProperty(e) || (e = /^\s*</.test(t) ? "default-markup": "default-code"),
                    O[e]
                }
                function d(e) {
                    var t = e.h;
                    try {
                        var i = n(e.c, e.i),
                        r = i.a;
                        e.a = r,
                        e.d = i.d,
                        e.e = 0,
                        u(t, r)(e);
                        var s = /\bMSIE\s(\d+)/.exec(navigator.userAgent),
                        s = s && 8 >= +s[1],
                        t = /\n/g,
                        o = e.a,
                        a = o.length,
                        i = 0,
                        l = e.d,
                        h = l.length,
                        r = 0,
                        c = e.g,
                        d = c.length,
                        f = 0;
                        c[d] = a;
                        var m, g;
                        for (g = m = 0; d > g;) c[g] !== c[g + 2] ? (c[m++] = c[g++], c[m++] = c[g++]) : g += 2;
                        for (d = m, g = m = 0; d > g;) {
                            for (var y = c[g], v = c[g + 1], b = g + 2; d >= b + 2 && c[b + 1] === v;) b += 2;
                            c[m++] = y,
                            c[m++] = v,
                            g = b
                        }
                        c.length = m;
                        var L, x = e.c;
                        x && (L = x.style.display, x.style.display = "none");
                        try {
                            for (; h > r;) {
                                var C, w = l[r + 2] || a,
                                S = c[f + 2] || a,
                                b = Math.min(w, S),
                                O = l[r + 1];
                                if (1 !== O.nodeType && (C = o.substring(i, b))) {
                                    s && (C = C.replace(t, "\r")),
                                    O.nodeValue = C;
                                    var _ = O.ownerDocument,
                                    E = _.createElement("span");
                                    E.className = c[f + 1];
                                    var k = O.parentNode;
                                    k.replaceChild(E, O),
                                    E.appendChild(O),
                                    w > i && (l[r + 1] = O = _.createTextNode(o.substring(b, w)), k.insertBefore(O, E.nextSibling))
                                }
                                i = b,
                                i >= w && (r += 2),
                                i >= S && (f += 2)
                            }
                        } finally {
                            x && (x.style.display = L)
                        }
                    } catch(T) {
                        p.console && console.log(T && T.stack || T)
                    }
                }
                var p = window,
                f = ["break,continue,do,else,for,if,return,while"],
                m = [[f, "auto,case,char,const,default,double,enum,extern,float,goto,inline,int,long,register,short,signed,sizeof,static,struct,switch,typedef,union,unsigned,void,volatile"], "catch,class,delete,false,import,new,operator,private,protected,public,this,throw,true,try,typeof"],
                g = [m, "alignof,align_union,asm,axiom,bool,concept,concept_map,const_cast,constexpr,decltype,delegate,dynamic_cast,explicit,export,friend,generic,late_check,mutable,namespace,nullptr,property,reinterpret_cast,static_assert,static_cast,template,typeid,typename,using,virtual,where"],
                y = [m, "abstract,assert,boolean,byte,extends,final,finally,implements,import,instanceof,interface,null,native,package,strictfp,super,synchronized,throws,transient"],
                v = [y, "as,base,by,checked,decimal,delegate,descending,dynamic,event,fixed,foreach,from,group,implicit,in,internal,into,is,let,lock,object,out,override,orderby,params,partial,readonly,ref,sbyte,sealed,stackalloc,string,select,uint,ulong,unchecked,unsafe,ushort,var,virtual,where"],
                m = [m, "debugger,eval,export,function,get,null,set,undefined,var,with,Infinity,NaN"],
                b = [f, "and,as,assert,class,def,del,elif,except,exec,finally,from,global,import,in,is,lambda,nonlocal,not,or,pass,print,raise,try,with,yield,False,True,None"],
                L = [f, "alias,and,begin,case,class,def,defined,elsif,end,ensure,false,in,module,next,nil,not,or,redo,rescue,retry,self,super,then,true,undef,unless,until,when,yield,BEGIN,END"],
                x = [f, "as,assert,const,copy,drop,enum,extern,fail,false,fn,impl,let,log,loop,match,mod,move,mut,priv,pub,pure,ref,self,static,struct,true,trait,type,unsafe,use"],
                f = [f, "case,done,elif,esac,eval,fi,function,in,local,set,then,until"],
                C = /^(DIR|FILE|vector|(de|priority_)?queue|list|stack|(const_)?iterator|(multi)?(set|map)|bitset|u?(int|float)\d*)\b/,
                w = /\S/,
                S = l({
                    keywords: [g, v, m, "caller,delete,die,do,dump,elsif,eval,exit,foreach,for,goto,if,import,last,local,my,next,no,our,print,package,redo,require,sub,undef,unless,until,use,wantarray,while,BEGIN,END", b, L, f],
                    hashComments: !0,
                    cStyleComments: !0,
                    multiLineStrings: !0,
                    regexLiterals: !0
                }),
                O = {};
                c(S,["default-code"]),
				c(a([],[["pln",/^[^<?]+/],["dec",/^<!\w[^>]*(?:>|$)/],["com",/^<\!--[\S\s]*?(?:--\>|$)/],["lang-",/^<\?([\S\s]+?)(?:\?>|$)/],["lang-",/^<%([\S\s]+?)(?:%>|$)/],["pun",/^(?:<[%?]|[%?]>)/],["lang-",/^<xmp\b[^>]*>([\S\s]+?)<\/xmp\b[^>]*>/i],["lang-js",/^<script\b[^>]*>([\S\s]*?)(<\/script\b[^>]*>)/i],["lang-css",/^<style\b[^>]*>([\S\s]*?)(<\/style\b[^>]*>)/i],["lang-in.tag",/^(<\/?[a-z][^<>]*>)/i]]),["default-markup","htm","html","mxml","xhtml","xml","xsl"]),
				c(a([["pln",/^\s+/,e," 	\r\n"],["atv",/^(?:"[^"]*"?|'[^']*'?)/,e,"\"'"]],[["tag",/^^<\/?[a-z](?:[\w-.:]*\w)?|\/?>$/i],["atn",/^(?!style[\s=]|on)[a-z](?:[\w:-]*\w)?/i],["lang-uq.val",/^=\s*([^\s"'>]*(?:[^\s"'/>]|\/(?=\s)))/],["pun",/^[/<->]+/],["lang-js",/^on\w+\s*=\s*"([^"]+)"/i],["lang-js",/^on\w+\s*=\s*'([^']+)'/i],["lang-js",/^on\w+\s*=\s*([^\s"'>]+)/i],["lang-css",/^style\s*=\s*"([^"]+)"/i],["lang-css",/^style\s*=\s*'([^']+)'/i],["lang-css",/^style\s*=\s*([^\s"'>]+)/i]]),["in.tag"]),
				c(a([],[["atv",/^[\S\s]+/]]),["uq.val"]),
                c(l({
                    keywords: g,
                    hashComments: !0,
                    cStyleComments: !0,
                    types: C
                }), ["c", "cc", "cpp", "cxx", "cyc", "m"]),
                c(l({
                    keywords: "null,true,false"
                }), ["json"]),
                c(l({
                    keywords: v,
                    hashComments: !0,
                    cStyleComments: !0,
                    verbatimStrings: !0,
                    types: C
                }), ["cs"]),
                c(l({
                    keywords: y,
                    cStyleComments: !0
                }), ["java"]),
                c(l({
                    keywords: f,
                    hashComments: !0,
                    multiLineStrings: !0
                }), ["bash", "bsh", "csh", "sh"]),
                c(l({
                    keywords: b,
                    hashComments: !0,
                    multiLineStrings: !0,
                    tripleQuotedStrings: !0
                }), ["cv", "py", "python"]),
                c(l({
                    keywords: "caller,delete,die,do,dump,elsif,eval,exit,foreach,for,goto,if,import,last,local,my,next,no,our,print,package,redo,require,sub,undef,unless,until,use,wantarray,while,BEGIN,END",
                    hashComments: !0,
                    multiLineStrings: !0,
                    regexLiterals: 2
                }), ["perl", "pl", "pm"]),
                c(l({
                    keywords: L,
                    hashComments: !0,
                    multiLineStrings: !0,
                    regexLiterals: !0
                }), ["rb", "ruby"]),
                c(l({
                    keywords: m,
                    cStyleComments: !0,
                    regexLiterals: !0
                }), ["javascript", "js"]),
                c(l({
                    keywords: "all,and,by,catch,class,else,extends,false,finally,for,if,in,is,isnt,loop,new,no,not,null,of,off,on,or,return,super,then,throw,true,try,unless,until,when,while,yes",
                    hashComments: 3,
                    cStyleComments: !0,
                    multilineStrings: !0,
                    tripleQuotedStrings: !0,
                    regexLiterals: !0
                }), ["coffee"]),
                c(l({
                    keywords: x,
                    cStyleComments: !0,
                    multilineStrings: !0
                }), ["rc", "rs", "rust"]),
                c(a([], [["str", /^[\S\s]+/]]), ["regex"]);
                var _ = p.PR = {
                    createSimpleLexer: a,
                    registerLangHandler: c,
                    sourceDecorator: l,
                    PR_ATTRIB_NAME: "atn",
                    PR_ATTRIB_VALUE: "atv",
                    PR_COMMENT: "com",
                    PR_DECLARATION: "dec",
                    PR_KEYWORD: "kwd",
                    PR_LITERAL: "lit",
                    PR_NOCODE: "nocode",
                    PR_PLAIN: "pln",
                    PR_PUNCTUATION: "pun",
                    PR_SOURCE: "src",
                    PR_STRING: "str",
                    PR_TAG: "tag",
                    PR_TYPE: "typ",
                    prettyPrintOne: function(e, t, i) {
                        var n = document.createElement("div");
                        return n.innerHTML = "<pre>" + e + "</pre>",
                        n = n.firstChild,
                        i && h(n, i, !0),
                        d({
                            h: t,
                            j: i,
                            c: n,
                            i: 1
                        }),
                        n.innerHTML
                    },
                    prettyPrint: t = t = function(t, i) {
                        function n() {
                            for (var i = p.PR_SHOULD_USE_CONTINUATION ? m.now() + 250 : 1 / 0; l.length > y && i > m.now(); y++) {
                                for (var s = l[y], c = S, u = s; u = u.previousSibling;) {
                                    var f = u.nodeType,
                                    O = (7 === f || 8 === f) && u.nodeValue;
                                    if (O ? !/^\??prettify\b/.test(O) : 3 !== f || /\S/.test(u.nodeValue)) break;
                                    if (O) {
                                        c = {},
                                        O.replace(/\b(\w+)=([\w%+\-.:]+)/g,
                                        function(e, t, i) {
                                            c[t] = i
                                        });
                                        break
                                    }
                                }
                                if (u = s.className, (c !== S || b.test(u)) && !L.test(u)) {
                                    for (f = !1, O = s.parentNode; O; O = O.parentNode) if (w.test(O.tagName) && O.className && b.test(O.className)) {
                                        f = !0;
                                        break
                                    }
                                    if (!f) {
                                        if (s.className += " prettyprinted", f = c.lang, !f) {
                                            var _, f = u.match(v); ! f && (_ = o(s)) && C.test(_.tagName) && (f = _.className.match(v)),
                                            f && (f = f[1])
                                        }
                                        if (x.test(s.tagName)) O = 1;
                                        else var O = s.currentStyle,
                                        E = a.defaultView,
                                        O = (O = O ? O.whiteSpace: E && E.getComputedStyle ? E.getComputedStyle(s, e).getPropertyValue("white-space") : 0) && "pre" === O.substring(0, 3);
                                        c.linenums = 1,//pengchuan add
										E = c.linenums,
                                        (E = "true" === E || +E) || (E = (E = u.match(/\blinenums\b(?::(\d+))?/)) ? E[1] && E[1].length ? +E[1] : !0 : !1),
                                        E && h(s, E, O),
                                        g = {
                                            h: f,
                                            c: s,
                                            j: E,
                                            i: O
                                        },
                                        d(g)
                                    }
                                }
                            }
                            l.length > y ? r(n, 250) : "function" == typeof t && t()
                        }
                        for (var s = i || document.body,
                        a = s.ownerDocument || document,
                        s = [s.getElementsByTagName("pre"), s.getElementsByTagName("code"), s.getElementsByTagName("xmp")], l = [], c = 0; s.length > c; ++c) for (var u = 0,
                        f = s[c].length; f > u; ++u) l.push(s[c][u]);
                        var s = e,
                        m = Date;
                        m.now || (m = {
                            now: function() {
                                return + new Date
                            }
                        });
                        var g, y = 0,
                        v = /\blang(?:uage)?-([\w.]+)(?!\S)/,
                        b = /\bprettyprint\b/,
                        L = /\bprettyprinted\b/,
                        x = /pre|xmp/i,
                        C = /^code$/i,
                        w = /^(?:pre|code|xmp)$/i,
                        S = {};
                        n()
                    }
                };
                "function" == typeof define && define.amd && define("google-code-prettify", [],
                function() {
                    return _
                })
            } (),
            t
        } ();
        y || r(i, 0)
    })()
} ();