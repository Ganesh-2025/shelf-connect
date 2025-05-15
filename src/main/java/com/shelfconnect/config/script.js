const fs = require('fs')
const data = []
const types = [
    'College Books (Higher Education Textbooks)',
    'Exam/Test Preparation Books',
    'Reading Books (Novels, Children, Business, Literature, History, etc.)',
    'School Books (up to 12th)'
]
const collect = async () => {

    for(let type of types) {
        const URL = prepareUrl(type);
        const res = await fetch(URL);
        const data = await res.json();
        loadData(data,type);
    }
}
const loadData = (categs,type)=>{
    categs.forEach(categ=>{
        data.push({
            name:categ.id,
            type
        });
    });
}
const prepareUrl = (type)=>{
    return `https://www.clankart.com/book_category/findCategories?${type}&_type=query`
}

collect().then(()=>{
    fs.writeFileSync("./data.json",JSON.stringify(data));
});