import React,{ useState, useEffect }  from "react";
import {useParams} from "react-router";
import {
    Table, Tag, Space, Button, Col, Row, Input, Form, Modal, InputNumber, TimePicker, DatePicker,
    Select, Divider, Tabs, message, AutoComplete
} from 'antd';
import {Link} from "react-router-dom";
import {doCall} from "../../router/api";
import {SearchOutlined} from "@ant-design/icons";
const { Column } = Table;
const { Option } = Select;
const { Search } = Input;
const { TabPane } = Tabs;





function Event() {
    //const { clubId } = useParams();
    const path = process.env.REACT_APP_API_BASE_URL
    //const { id } = useParams();
    const [allEvents, setAllEvents] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedEvent, setSelectedEvent] = useState(null);
    const [rsvpedEvents, setRsvpedEvents] = useState([]);
    // const [rsvpedEvents] = useState([
    //     { id: 1, title: 'AI Seminar', description: 'Discussing AI trends', date: '2024-09-10', time: '14:00', venueName: 'Room A'},
    //     { id: 2, title: 'React Workshop', description: 'Learn React basics', date: '2024-09-12', time: '10:00', venueName: 'Room B' },
    // ]);
    const [clubs, setClubs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [form] = Form.useForm();
    const [studentSearchResults, setStudentSearchResults] = useState([]);
    const [participants, setParticipants] = useState([{ name: '', email: '' }]);
    const [participantsIds, setParticipantsIds] = useState([]);

    useEffect(() => {
        const fetchAllEvent = async () => {
            try {
                const res = await doCall(`${path}/student/events/?id=-1`,'GET', );

                // const response = await fetch(`${path}/student/events/?id=-1`, {
                //     method: 'GET',
                //     headers: {
                //         'Content-Type': 'application/json', // 设置内容类型
                //         'Authorization': `${type} ${token}`, // 使用 Bearer token 进行身份验证
                //     }
                // });
                if (res.ok) {
                    const data = await res.json();
                    setAllEvents(data);
                    console.log(data, 'data------');
                } else {
                    setError('Failed to load club information');
                }
            } catch (error) {
                console.error('Error:', error);
                setError('An error occurred while fetching the club information');
            } finally {
                setLoading(false);
            }
        };

            fetchAllEvent();
        }, []);
    //
    useEffect(() => {
        const fetchRsvpedEvents = async () => {
            try {
                const res = await doCall(`${path}/student/userdetailed/tickets`, 'GET');
                if (res.ok) {
                    const data = await res.json();
                    setRsvpedEvents(data);
                }
            } catch (error) {
                console.error('Error fetching RSVPed events:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchRsvpedEvents();
    }, []);

    // useEffect(() => {
    //     const fetchClubs = async () => {
    //         try {
    //             const response = await fetch(`${path}/student/clubs/?id=-1`);
    //             if (response.ok) {
    //                 const data = await response.json();
    //                 setClubs(data);
    //             } else {
    //                 setError('Failed to load club information');
    //             }
    //         } catch (error) {
    //             console.error('Error:', error);
    //             setError('An error occurred while fetching the club information');
    //         }
    //     };
    //
    //     fetchClubs();
    // }, []);
    const getStatusTagColor = (status) => {
        if (status === 'Issued') return 'green';
        if (status === 'pending') return 'geekblue';
        return 'volcano';
    };




    // const handleRsvp = async (eventId) => {
    //     try {
    //         const res = await doCall(`${path}/student/events/rsvp/${eventId}`, 'POST');
    //         if (res.ok) {
    //             const updatedEvent = allEvents.find(event => event.id === eventId);
    //             setRsvpedEvents([...rsvpedEvents, updatedEvent]); // Add the event to RSVPed list
    //         }
    //     } catch (error) {
    //         console.error('Error RSVPing to event:', error);
    //     }
    // };


    // const showModal = () => {
    //     setIsModalVisible(true);
    // };
    //
    // const handleCancel = () => {
    //     setIsModalVisible(false);
    // };
    //
    // const handleCreate = async (values) => {
    //     try {
    //         const newEvent = {
    //             title: values.title,
    //             description: values.description,
    //             date: values.date.format('YYYY-MM-DD'),
    //             time: values.time.format('HH:mm:ss'),
    //             venueName: values.venueName,
    //             cost: values.cost,
    //             clubId: 1
    //         };
    //         const res = await doCall(`${path}/student/events/save`,'POST',{newEvent} );
    //
    //         // const response = await fetch(`${path}/student/events/save`, {
    //         //     method: 'POST',
    //         //     headers: {
    //         //         'Content-Type': 'application/json',
    //         //         'Authorization': `${type} ${token}`, // 使用 Bearer token 进行身份验证
    //         //     },
    //         //     body: JSON.stringify(newEvent),
    //         // });
    //
    //         if (res.ok) {
    //             const createdEvent = await res.json();
    //             setEvents([...events, createdEvent]);  // Add the new event to the list
    //             setIsModalVisible(false);
    //             form.resetFields();
    //         } else {
    //             //console.log(clubId)
    //             console.error('Failed to create event');
    //         }
    //     } catch (error) {
    //         console.error('Error creating event:', error);
    //     }
    // };
    const handleViewTicket = (eventId) => {
        // Add functionality to handle viewing ticket here
        console.log(`Viewing ticket for event ID: ${eventId}`);

    };

    const handleSearch = async (value) => {
        setSearchTerm(value.toLowerCase());
        try {
            const res = await doCall(`${path}/student/events/search?title=${value}`, 'GET');
            if (res.ok) {
                const data = await res.json();
                setAllEvents(data); // Update the event list with the search result
            } else {
                console.error('Error fetching events based on search:', res.statusText);
            }
        } catch (error) {
            console.error('Search error:', error);
        }
    };

    const handleOpenModal = (eventId) => {
        console.log("Opening modal for event ID:", eventId);
        setSelectedEvent(eventId);
        setIsModalVisible(true);
    };

    const handleCloseModal = () => {
        setIsModalVisible(false);  // Hide the modal
    };
    // Submit the RSVP request with participant details
    const handleRsvpSubmit = async (values) => {
        if (values.numTickets !== participantsIds.length) {
            message.error('Number of tickets must match the number of participants.');
            return;
        }
        try {
            const submitterId = values.submitterId;  // Assuming we fetch submitter's student ID separately
            const res = await doCall(`${path}/student/events/applyRSVP`, 'POST', {
                eventId: selectedEvent,
                studentId: submitterId,
                numTickets: values.numTickets,
                participants_id: participantsIds,  // Send participant IDs stored internally
            });

            if (res.ok) {
                const updatedEvent = allEvents.find(event => event.id === selectedEvent);
                setRsvpedEvents([...rsvpedEvents, updatedEvent]);
                handleCloseModal();
                form.resetFields();
                setParticipantsIds([]); // Clear participants after submission
            } else {
                console.error('Error applying for RSVP:', res.statusText);
            }
        } catch (error) {
            console.error('Error applying for RSVP:', error);
        }
    };


    const handleSearchStudent = async (value) => {
        if (value) {
            const res = await doCall(`${path}/student/students/?query=${value}`, 'GET');
            const studentsData = await res.json();
            setStudentSearchResults(studentsData);
        }
    };
    // Handle selection of a student from the AutoComplete dropdown
    const handleSelectStudent = (value, index) => {
        const selectedStudent = studentSearchResults.find(student => student.email === value);
        if (selectedStudent) {
            const participants = form.getFieldValue('participants') || [];
            participants[index] = { ...participants[index], email: selectedStudent.email };
            form.setFieldsValue({ participants });

            // Store the student ID for backend submission
            setParticipantsIds(prevIds => {
                const updatedIds = [...prevIds];
                updatedIds[index] = selectedStudent.id;
                return updatedIds;
            });
        }
    };


    const isEventRsvped = (eventId) => {
        return rsvpedEvents.some(event => event.eventId === eventId);
    };




    return (
        <Tabs defaultActiveKey="1">
            {/* Tab 1: RSVPed Events */}
            <TabPane tab="My Events" key="1">
                <Row justify="center" style={{ marginBottom: 16 }}>
                    <Col span={24}>
                        {/*<h2>My Events</h2>*/}
                        <Table dataSource={rsvpedEvents} rowKey="id">
                            <Column
                                title="Title"
                                dataIndex="title"
                                key="title"
                                render={text => <span style={{ color: 'royalblue', textDecoration: 'underline', cursor: 'pointer' }}>{text}</span>} // Title in blue
                            />
                            <Column title="Date" dataIndex="date" key="date" />
                            <Column title="Time" dataIndex="time" key="time" />
                            <Column title="Venue" dataIndex="venueName" key="venueName" />
                            <Column
                                title="Status"
                                dataIndex="ticketStatus"
                                key="ticketStatus"
                                render={ticketStatus => <Tag color={getStatusTagColor(ticketStatus)}>{ticketStatus}</Tag>}
                            />
                            <Column
                                title="Action"
                                key="action"
                                render={(_, record) => (
                                    <Button type="primary" onClick={() => handleViewTicket(record.id)}>
                                        View Ticket
                                    </Button>
                                )}
                            />
                        </Table>
                    </Col>
                </Row>
            </TabPane>

            {/* Tab 2: All Events */}
            <TabPane tab="All Events" key="2">
                <Row justify="center" style={{ marginBottom: 16 }}>
                    <Col span={24}>
                        {/*<h2>All Events</h2>*/}
                        <Search
                            placeholder="Search events"
                            allowClear
                            onSearch={handleSearch}
                            enterButton
                            style={{ marginBottom: 16 }}
                        />
                        <Table
                            dataSource={allEvents.filter(event =>
                                event.title.toLowerCase().includes(searchTerm) ||
                                event.description.toLowerCase().includes(searchTerm)
                            )}
                            rowKey="id"
                        >
                            <Column title="Title" dataIndex="title" key="title" />
                            <Column title="Description" dataIndex="description" key="description" />
                            <Column title="Date" dataIndex="date" key="date" />
                            <Column title="Time" dataIndex="time" key="time" />
                            <Column title="Venue" dataIndex="venueName" key="venueName" />
                            <Column
                                title="Action"
                                key="action"
                                render={(_, record) => (
                                    isEventRsvped(record.id) ? (
                                        <Button type="default" disabled>
                                            Already Joined
                                        </Button>
                                    ) : (
                                        <Button  onClick={() => handleOpenModal(record.id)}>
                                            Get Ticket
                                        </Button>
                                    )
                                )}
                            />
                        </Table>
                    </Col>
                </Row>
                {/* Modal for getting tickets */}
                <Modal
                    title="Get Ticket"
                    visible={isModalVisible}
                    onCancel={handleCloseModal}
                    footer={null}
                >
                    <Form form={form} onFinish={handleRsvpSubmit}>
                        <Form.Item
                            name="numTickets"
                            label="Number of Tickets"
                            rules={[{ required: true, message: 'Please input the number of tickets!' }]}
                        >
                            <InputNumber min={1} max={5} />
                        </Form.Item>

                        <Form.List name="participants">
                            {(fields, { add, remove }) => (
                                <>
                                    {fields.map(({ key, name, fieldKey, ...restField }, index) => (
                                        <Row key={key} gutter={16} align="middle">
                                            <Col span={16}>
                                                <Form.Item
                                                    {...restField}
                                                    name={[name, 'email']}
                                                    fieldKey={[fieldKey, 'email']}
                                                    rules={[{ required: true, message: 'Please search and select a participant!' }]}
                                                    label="Search Participant by Email"
                                                >
                                                    <AutoComplete
                                                        placeholder="Enter email"
                                                        onSearch={handleSearchStudent}
                                                        onSelect={(value) => handleSelectStudent(value, index)}
                                                        options={studentSearchResults.map(student => ({
                                                            label: `${student.name} (${student.email})`,
                                                            value: student.email
                                                        }))}
                                                    />
                                                </Form.Item>
                                            </Col>
                                            <Col span={4}>
                                                <Button type="link" onClick={() => remove(name)} danger>
                                                    Remove
                                                </Button>
                                            </Col>
                                        </Row>
                                    ))}
                                    <Form.Item>
                                        <Button type="dashed" onClick={() => add()} block>
                                            Add Participant
                                        </Button>
                                    </Form.Item>
                                </>
                            )}
                        </Form.List>

                        <Form.Item>
                            <Row justify="center">
                                <Button type="primary" htmlType="submit">
                                    Submit RSVP
                                </Button>
                            </Row>
                        </Form.Item>
                    </Form>
                </Modal>
            </TabPane>
        </Tabs>
    );
}

export default Event;