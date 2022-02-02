// from
function renderPerson(outStream, person) {
  const result = [];
  ...
  result.push(`<p>제목: ${person.photo.title}</p>`); // 제목 출력(중복코드)
  result.push(emitPhotoData(person.photo);
  ...
}
function photoDiv(p) {
  return [
    '<div>',
    `<p>제목: ${p.title}</p>`, // 제목 출력(중복 코드)
    emitPhotoData(p),
    '</div>'
  ].join('/n');
}
function emitPhotoData(aPhoto) {
  const result = [];
  result.push(`<p>위치: ${aPhoto.location}</p>`);
  result.push(`<p>날짜: ${aPhoto.date.toDateString()}</p>`);
  ...
  return result.join('/n');
}
------------------------------------------------
// to
function renderPerson(outStream, person) {
  const result = [];
  ...
  result.push(emitPhotoData(person.photo);
  ...
}
function photoDiv(p) {
  return [
    '<div>',
    emitPhotoData(p),
    '</div>'
  ].join('/n');
}
function emitPhotoData(aPhoto) {
  const result = [];
  result.push(`<p>제목: ${aPhoto.title}</p>`);
  result.push(`<p>위치: ${aPhoto.location}</p>`);
  result.push(`<p>날짜: ${aPhoto.date.toDateString()}</p>`);
  ...
  return result.join('/n');
}