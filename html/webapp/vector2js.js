(function () {
	if (typeof window != "undefined") {
		var a = window.vector;
		window.vector = function () {
			this.abc = 123;
		}
		window.vector.prototype.fn = function() {
			var cash = @com.quexten.ravtech.Cash::new()();
			return cash@com.quexten.ravtech.Cash::test()();
		};
		window.vector.abc = 123;
	}
})();
var vector = vector || {};