// Compiled by ClojureScript 1.12.145 {:static-fns true, :target :nodejs, :nodejs-rt true, :optimizations :none}
goog.provide('diagnostic.arity');
goog.require('cljs.core');
diagnostic.arity.exec_at = (function diagnostic$arity$exec_at(var_args){
var G__29 = arguments.length;
switch (G__29) {
case 2:
return diagnostic.arity.exec_at.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return diagnostic.arity.exec_at.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(diagnostic.arity.exec_at.cljs$core$IFn$_invoke$arity$2 = (function (cwd,args){
return diagnostic.arity.exec_at.cljs$core$IFn$_invoke$arity$3(cwd,args,cljs.core.PersistentArrayMap.EMPTY);
}));

(diagnostic.arity.exec_at.cljs$core$IFn$_invoke$arity$3 = (function (_cwd,_args,_opts){
return new cljs.core.Keyword(null,"original","original",-445386197);
}));

(diagnostic.arity.exec_at.cljs$lang$maxFixedArity = 3);

diagnostic.arity.git_value = (function diagnostic$arity$git_value(path,args){
return diagnostic.arity.exec_at.cljs$core$IFn$_invoke$arity$2(path,args);
});
diagnostic.arity.delayed_git = (function diagnostic$arity$delayed_git(_path,_args){
return new cljs.core.Keyword(null,"replacement","replacement",-1836238839);
});
diagnostic.arity._main = (function diagnostic$arity$_main(){
var control = diagnostic.arity.git_value("unused",cljs.core.PersistentVector.EMPTY);
var original_property_QMARK_ = cljs.core.fn_QMARK_((diagnostic.arity.exec_at["cljs$core$IFn$_invoke$arity$2"]));
var replacement_property_QMARK_ = cljs.core.fn_QMARK_((diagnostic.arity.delayed_git["cljs$core$IFn$_invoke$arity$2"]));
var observed = (function (){var exec_at_orig_val__31 = diagnostic.arity.exec_at;
var exec_at_temp_val__32 = diagnostic.arity.delayed_git;
(diagnostic.arity.exec_at = exec_at_temp_val__32);

try{try{return new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"returned","returned",-2020439163),diagnostic.arity.git_value("unused",cljs.core.PersistentVector.EMPTY)], null);
}catch (e33){var error = e33;
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"error-name","error-name",-461307167),error.name,new cljs.core.Keyword(null,"message","message",-406056002),error.message], null);
}}finally {(diagnostic.arity.exec_at = exec_at_orig_val__31);
}})();
var restored = diagnostic.arity.git_value("unused",cljs.core.PersistentVector.EMPTY);
var reproduced_QMARK_ = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"original","original",-445386197),control)) && (((original_property_QMARK_) && ((((!(replacement_property_QMARK_))) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2("TypeError",new cljs.core.Keyword(null,"error-name","error-name",-461307167).cljs$core$IFn$_invoke$arity$1(observed))) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"original","original",-445386197),restored)))))))));
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([JSON.stringify(cljs.core.clj__GT_js(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"schema","schema",-1582001791),new cljs.core.Keyword(null,"production-git-processes-started","production-git-processes-started",-400623901),new cljs.core.Keyword(null,"static-fns","static-fns",-501950748),new cljs.core.Keyword(null,"optimizations","optimizations",-2047476854),new cljs.core.Keyword(null,"mechanism-reproduced","mechanism-reproduced",-2142852118),new cljs.core.Keyword(null,"node","node",581201198),new cljs.core.Keyword(null,"original-arity-property","original-arity-property",1593448080),new cljs.core.Keyword(null,"control","control",1892578036),new cljs.core.Keyword(null,"production-test-suite-executed","production-test-suite-executed",-430771212),new cljs.core.Keyword(null,"original-restored","original-restored",2059021685),new cljs.core.Keyword(null,"source-or-test-repair","source-or-test-repair",-2067221928),new cljs.core.Keyword(null,"replacement-observation","replacement-observation",-1271341192),new cljs.core.Keyword(null,"replacement-arity-property","replacement-arity-property",-1762348289)],["codex.cljs-arity-representation-probe/v1",(0),true,"none",reproduced_QMARK_,process.version,original_property_QMARK_,cljs.core.name(control),false,cljs.core.name(restored),false,observed,replacement_property_QMARK_])))], 0));

if(reproduced_QMARK_){
return null;
} else {
return (process.exitCode = (1));
}
});
(cljs.core._STAR_main_cli_fn_STAR_ = diagnostic.arity._main);

//# sourceMappingURL=arity.js.map
