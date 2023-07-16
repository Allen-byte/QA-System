// 全局工具文件

export const utils = {
    //获取当前日期，格式YYYY-MM-DD
    getNowFormatDay(nowDate) {
        var char = "-";
        if (nowDate == null) {
            nowDate = new Date();
        }
        var day = nowDate.getDate();
        var month = nowDate.getMonth() + 1;//注意月份需要+1
        var year = nowDate.getFullYear();
        //补全0，并拼接
        return year + char + this.completeDate(month) + char + this.completeDate(day);
    },
    //补全0
    completeDate(value) {
        return value < 10 ? "0" + value : value;
    },
    //获取当前时间，格式YYYY-MM-DD HH:mm:ss
    getNowFormatTime() {
        var nowDate = new Date();
        var colon = ":";
        var h = nowDate.getHours();
        var m = nowDate.getMinutes();
        var s = nowDate.getSeconds();
        //补全0，并拼接
        return this.getNowFormatDay(nowDate) + " " + this.completeDate(h) + colon + this.completeDate(m) + colon + this.completeDate(s);
    },
    //计算日期差
    dateDiff(oldDate) {
        var old_time = new Date(oldDate);
        var new_time = new Date();

        var difftime = (new_time - old_time) / 1000;
        var day = parseInt(difftime / 86400);
        return day + 1 + '';
    },
    // 设置markdown内容中图片的宽高
    setImageSize(text, width, height = "auto") {
        if (text == "") {
            return text;
        }
        let arr = text.split(")");
        let res = "";
        arr.forEach((item, i) => {
            if (item.startsWith("![Description](") || item.includes("![Description](")) {
                res += item + `){{{width="${width}" height="${height}"}}}`;
            } else {
                res += item;
            }
        })
        return res;
    },
}