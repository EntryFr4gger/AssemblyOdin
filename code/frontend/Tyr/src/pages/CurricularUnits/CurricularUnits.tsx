import * as React from 'react';
import { useState } from 'react';
import { Table } from 'react-bootstrap';
import { Course } from '../../model/Course';


const courses: Course[] = [
  {
    id: 1,
    title: 'Code VI',
    description: 'An advanced programming course covering various software engineering principles.',
    classes: [

      {
        id: 1,
        teacher:{name : 'Tomás Santos'},
        date: new Date('10/10/2024'),
        summary: 'Covered basic syntax and script execution.',
        course: null,
        students: []
      },
      {
        id: 2,
        teacher: {name : 'Tomás Santos'},
        date: new Date('5/10/2024'),
        summary: 'Covered basic syntax and script execution.',
        course: null,
        students: []
      },
    ],
  },
  {
    id: 2,
    title: 'Python Game Maker',
    description: 'Learn game development fundamentals using Python and Pygame.',
    classes: [
      {
        id:3,
        teacher: {name : 'Ana Silva'},
        date: new Date('11/13/2024'),
        summary: 'Covered basic syntax and script execution.',
        course: null,
        students: []
      },
      {
        id:4,
        teacher: {name : 'Gil Reis'},
        date: new Date('1/18/2024'),
        summary: 'Covered basic syntax and script execution.',
        course: null,
        students: []
      },
    ],
  },
  {
    id: 3,
    title: 'Data Structures',
    description: 'A deep dive into data structures, from the basics to complex applications.',
    classes: [
      {
        id:5,
        teacher: {name : 'Carlos Pereira'},
        date:  new Date('11/15/2024'),
        summary: 'Covered basic syntax and script execution.',
        course: null,
        students: []
      },
      {
        id:6,
        teacher: {name : 'Carlos Pereira'},
        date:  new Date('4/19/2024'),
        summary: 'Covered basic syntax and script execution.',
        course: null,
        students: []
      },
    ],
  },
];


enum FilterOptions {
  Legacy = 'Legacy',
  Ongoing = 'Ongoing',
  ToStart = 'Upcoming'
}

interface DetailViewProps {
  course: Course;
}

const DetailView: React.FC<DetailViewProps> = ({course}) => {
  if (!course) return null;

  return (
    <div style={{ border: '1px solid grey', padding: '20px', marginTop: '20px' }}>
      <h4>{course.title}</h4>
      <p>{course.description}</p>
      <h5>Currently Scheduled Classes</h5>
      <Table striped bordered hover>
        <thead>
        <tr>
          <th>Teacher</th>
          <th>Date</th>
        </tr>
        </thead>
        <tbody>
        {course.classes.map((cls, index) => (
          <tr key={index}>
            <td>{cls.teacher.name}</td>
            <td>{cls.date.toLocaleDateString()}</td>
          </tr>
        ))}
        </tbody>
      </Table>
    </div>
  );
};

function CurricularUnits() {
  const [filter, setFilter] = useState(FilterOptions.Ongoing);
  const [selectedCourse, setSelectedCourse] = useState<Course | null>(null);

  const getUniqueTeachers = (course: Course) => {
    const teacherSet = new Set(course.classes.map(cls => cls.teacher.name));
    return Array.from(teacherSet).join(', ');
  };

  const getUniqueWeekdays = (course: Course) => {
    const dateSet = new Set(
      course.classes.map(cls => {
        const date = new Date(cls.date);
        return date.toLocaleString('en-US', { weekday: 'long' });
      })
    );
    return Array.from(dateSet).join(', ');
  };

  const renderCourseRow = (course: Course, index: number) => (
    <tr key={index} onClick={() => setSelectedCourse(course)}>
      <td>{course.title}</td>
      <td>{getUniqueTeachers(course)}</td>
      <td>{getUniqueWeekdays(course)}</td>
    </tr>
  );

  return (
    <div>
      <h3>Tec Courses</h3>
      <select
        className="form-select mb-3"
        value={filter}
        onChange={(e) => setFilter(e.target.value as FilterOptions)}
      >
        {Object.values(FilterOptions).map((option) => (
          <option key={option} value={option}>
            {option}
          </option>
        ))}
      </select>

      <Table striped bordered hover responsive>
        <thead>
        <tr>
          <th>Title</th>
          <th>Teachers</th>
          <th>Dates</th>
        </tr>
        </thead>
        <tbody>
        {courses.map(renderCourseRow)}
        </tbody>
      </Table>

      <DetailView course={selectedCourse}/>
    </div>
  );
}

export default CurricularUnits;
